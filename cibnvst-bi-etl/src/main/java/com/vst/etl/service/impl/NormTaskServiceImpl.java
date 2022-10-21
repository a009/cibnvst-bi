package com.vst.etl.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.vst.etl.entity.Task;
import com.vst.etl.repository.DorisRepository;
import com.vst.etl.repository.H2Repository;
import com.vst.etl.repository.MysqlRepository;
import com.vst.etl.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class NormTaskServiceImpl implements TaskService {
    private MysqlRepository mysqlRepository;
    private H2Repository h2Repository;
    private DorisRepository dorisRepository;

    @Override
    public void doAction(Task task, DateTime dateTime, Dict dict) {
        // 1.查询Doris
        List<Map<String, Object>> dorisRecords = dorisRepository.query(formatSql(task.getDorisQuery(), dict));

        // 2.验证数据有效性
        Assert.notEmpty(dorisRecords, "source data not empty");
        log.info("records size: {}", dorisRecords.size());

        try {
            //获取Map的Key转换成List
            List<String> columns = CollUtil.newArrayList(CollUtil.getFirst(dorisRecords).keySet());
            //创建表
            createTable(task.getTableName(), task.getMinusDay(), columns);
            //注册数据
            registerData(task.getTableName(), task.getDateField(), dateTime, task.getMinusDay(), columns, dorisRecords);

            //核心业务
            List<Map<String, Object>> data = h2Repository.query(formatSql(task.getH2Query(), dict));
            //插入数据
            mysqlRepository.deleteInsert(
                    task.getTableName(),
                    task.getDateField(),
                    dateTime.toString(DatePattern.PURE_DATE_FORMAT),
                    data
            );
        } catch (Exception e) {
            log.error("error:", e);
        } finally {
            destroyTable(task.getTableName(), task.getMinusDay());
        }
    }

    /**
     * 销毁表结构
     *
     * @param tableName
     * @param minusDay
     */
    private void destroyTable(String tableName, int[] minusDay) {
        Arrays.stream(minusDay).forEach(index -> {
            String historyTable = StrUtil.format("{}{}", tableName, Math.abs(index));
            h2Repository.dropTable(historyTable);
        });
        h2Repository.dropTable(tableName);
    }

    /**
     * 历史表函数
     *
     * @param tableName
     * @param minusDay
     * @param consumer
     */
    private void historyFunc(String tableName, int[] minusDay, BiConsumer<String, Integer> consumer) {
        Arrays.stream(minusDay)
                .forEach(historyDay -> {
                    String historyTable = StrUtil.format("{}{}", tableName, Math.abs(historyDay));
                    consumer.accept(historyTable, historyDay);
                });
    }

    /**
     * 注册数据
     */
    private void registerData(String tableName, String dateField, DateTime dateTime, int[] minusDay, List<String> columns, List<Map<String, Object>> dorisRecords) {
        historyFunc(tableName, minusDay, (historyTable, historyDay) -> {
            String historyDate = dateTime
                    .offsetNew(DateField.DAY_OF_YEAR, historyDay)
                    .toString(DatePattern.PURE_DATE_FORMAT);
            List<Map<String, Object>> data = mysqlRepository.query(tableName, dateField, historyDate, columns);
            h2Repository.insert(historyTable, data);
        });
        h2Repository.insert(tableName, dorisRecords);
    }

    /**
     * 创建表结构
     *
     * @param tableName
     * @param minusDay
     * @param columns
     */
    private void createTable(String tableName, int[] minusDay, List<String> columns) {
        List<Map<String, Object>> columnMap = mysqlRepository.scanSchema(tableName, columns);

        Map<String, String> columnInfo =
                columnMap.stream().map(entity -> {
                    String dataType = MapUtil.getStr(entity, "DATA_TYPE");
                    String columName = MapUtil.getStr(entity, "COLUMN_NAME");
                    return new Pair<>(columName, dataType);
                }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        h2Repository.createTable(tableName, columnInfo);

        historyFunc(tableName, minusDay, (historyTable, historyDay) -> {
            h2Repository.createTable(historyTable, columnInfo);
        });
    }
}