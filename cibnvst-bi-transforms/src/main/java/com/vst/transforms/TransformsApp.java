package com.vst.transforms;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.exceptions.CheckedUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.db.nosql.redis.RedisDS;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import com.vst.transforms.handler.IResolverHandler;
import com.vst.transforms.handler.ResolverHandlerManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import javax.sql.DataSource;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.vst.transforms.config.AppConfig.*;


public class TransformsApp {
    public static void main(String[] args) throws Exception {
        FlinkRuntime.run(args);
    }

    /**
     * 对数据做格式化操作
     */
    public static class FormatDataFunction extends RichMapFunction<JSONObject, JSONObject> {
        private transient ResolverHandlerManager resolverHandlerManager;

        @Override
        public void open(Configuration parameters) throws Exception {
            String ipPath = getRuntimeContext()
                    .getDistributedCache()
                    .getFile(IP_PATH_KEY)
                    .getAbsolutePath();

            String redisConfig = getRuntimeContext()
                    .getDistributedCache()
                    .getFile(REDIS_CONFIG_KEY)
                    .getAbsolutePath();

            Setting setting = new Setting(redisConfig);
            RedisDS redisDS = RedisDS.create(setting, null);

            resolverHandlerManager = ResolverHandlerManager.create()
                    .addResolverHandler(IResolverHandler.createAddressResolverHandler(ipPath))
                    .addResolverHandler(IResolverHandler.createMovieResolverHandler(redisDS))
                    .addResolverHandler(IResolverHandler.createTopicResolverHandler(redisDS));
        }

        @Override
        public void close() throws Exception {
            resolverHandlerManager.close();
        }

        @Override
        public JSONObject map(JSONObject value) {
            resolverHandlerManager.resolver(value);
            return value;
        }
    }

    /**
     * 字节数组反序列化成Json对象
     */
    @Slf4j
    public static class JsonDeserializationSchema implements DeserializationSchema<JSONObject> {

        @Override
        public JSONObject deserialize(byte[] message) {
            return JSONUtil.parseObj(StrUtil.str(message, CharsetUtil.UTF_8));
        }

        @Override
        public boolean isEndOfStream(JSONObject nextElement) {
            return false;
        }

        @Override
        public TypeInformation getProducedType() {
            return TypeInformation.of(JSONObject.class);
        }
    }

    /**
     * 获取表信息
     */
    public static class TableInfo implements Serializable {
        private List<Entity> list;
        private final String COLUMN_NAME = "COLUMN_NAME";
        private final String COLUMN_SIZE = "COLUMN_SIZE";
        private final String DATA_TYPE = "DATA_TYPE";

        public TableInfo(ParameterTool parameterTool) {
            setup(parameterTool);
        }

        private void setup(ParameterTool parameterTool) {
            //1.拆分：{schema}.{tableName}
            String[] dbAndName = StrUtil.splitToArray(TABLE_NAME_VALUE, ".");

            //2.条件
            Entity where = Entity
                    .create("columns")
                    .set("TABLE_SCHEMA", dbAndName[0])
                    .set("TABLE_NAME", dbAndName[1]);

            Setting setting = new Setting(parameterTool.getRequired(DB_CONFIG_KEY));
            DSFactory dsFactory = DSFactory.create(setting);
            DataSource dataSource = dsFactory.getDataSource();

            //3.查找
            this.list = CheckedUtil.uncheck(() -> Db.use(dataSource).find(where)).call();

            dsFactory.close();
        }

        private Object formatValue(Object value, String dataType, Integer columnSize) {
            Object newValue = Convert.convertQuietly(ObjectFormat.typeToClass(dataType), value);
            if (newValue instanceof Date) {
                return newValue.toString();
            } else if (newValue instanceof String) {
                return ObjectFormat.subStrUTF8(newValue.toString(), columnSize);
            } else {
                return newValue;
            }
        }

        public JSONObject formatToJson(JSONObject jsonObject) {
            JSONObject newJson = new JSONObject();
            for (Entity entity : list) {
                String columnName = entity.getStr(COLUMN_NAME);
                Integer columnSize = entity.getInt(COLUMN_SIZE);
                String dateType = entity.getStr(DATA_TYPE);

                Object value = jsonObject.get(columnName);

                newJson.putOpt(columnName, formatValue(value, dateType, columnSize));
            }
            return newJson;
        }
    }

    public static class ObjectFormat {
        /**
         * 字符串按UTF8字符截取
         */
        public static String subStrUTF8(String str, Integer len) {
            if (StrUtil.isNotEmpty(str) && ObjectUtil.isNotNull(len)) {
                byte[] bytes = StrUtil.bytes(str, StandardCharsets.UTF_8);
                if (bytes.length > len) {
                    int count = 0;
                    for (int x = len - 1; x >= 0; x--)
                        if (bytes[x] < 0) count++;
                        else break;
                    int subValue = count % 3 == 0 ? 0 : count % 3 == 1 ? 1 : 2;
                    return new String(bytes, 0, len - subValue, StandardCharsets.UTF_8);
                }
            }
            return str;
        }

        /**
         * 根据字符串类型转换成Java类型
         *
         * @param dataType
         * @return
         */
        public static Class<?> typeToClass(String dataType) {
            switch (dataType) {
                case "date":
                    return Date.class;
                case "datetime":
                    return DateTime.class;
                case "int":
                    return Integer.class;
                case "bigint":
                    return Long.class;
                case "tinyint":
                    return Boolean.class;
                default:
                    return String.class;
            }
        }
    }

    public static class FlinkRuntime {
        /**
         * Kafka To Doris
         *
         * @param args
         * @throws Exception
         */
        public static void run(String[] args) throws Exception {
            ParameterTool parameterTool = ParameterTool.fromArgs(args);
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.getConfig().setGlobalJobParameters(parameterTool);

            env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 3000));
            env.enableCheckpointing(TimeUnit.SECONDS.toMillis(parameterTool.getInt("checkpointInterval", 10)));

            env.registerCachedFile(parameterTool.getRequired(IP_PATH_KEY), IP_PATH_KEY);
            env.registerCachedFile(parameterTool.getRequired(REDIS_CONFIG_KEY), REDIS_CONFIG_KEY);

            OffsetsInitializer offsetsInitializer = StrUtil.equals("earliest", parameterTool.get("offset", "latest")) ?
                    OffsetsInitializer.earliest() : OffsetsInitializer.latest();

            // 1. 加载Kafka数据
            KafkaSource<JSONObject> kafkaSource =
                    KafkaSource.<JSONObject>builder()
                            .setGroupId(getParameterString(parameterTool, KAFKA_GROUP_ID_KEY))
                            .setTopics(getParameterList(parameterTool, SOURCE_TOPIC_KEY))
                            .setValueOnlyDeserializer(new JsonDeserializationSchema())
                            .setStartingOffsets(offsetsInitializer)
                            .setBootstrapServers(getParameterString(parameterTool, BOOTSTRAP_SERVERS_KEY))
                            .build();
            DataStream<JSONObject> kafkaSourceDS =
                    env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "KafkaSource")
                            .filter(MapUtil::isNotEmpty);

            TableInfo tableInfo = new TableInfo(parameterTool);

            KafkaSink<String> sink = KafkaSink.<String>builder()
                    .setBootstrapServers(getParameterString(parameterTool, BOOTSTRAP_SERVERS_KEY))
                    .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                            .setTopic(getParameterString(parameterTool, TARGET_TOPIC_KEY))
                            .setValueSerializationSchema(new SimpleStringSchema())
                            .build()
                    )
                    .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                    .build();

            // 3. 格式化数据
            DataStream<String> formatData = kafkaSourceDS
                    .map(new FormatDataFunction()).name("formatInfo")  //格式化影片、专题、IP信息
                    .map(tableInfo::formatToJson).name("formatType")   //格式化数据类型、长度
                    .map(JSONObject::toString);

            formatData.sinkTo(sink);

            env.execute(parameterTool.get("name", "kafkaToKafka"));
        }
    }
}
