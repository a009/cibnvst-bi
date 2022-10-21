package com.vst.etl.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.vst.etl.entity.TaskIterator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@AllArgsConstructor
@Data
public class JobConfig {
    private DateTime dateTime;
    private TaskIterator taskIterator;
    private Dict dict;

    public static JobConfig create(JSONArray jsonArray, CalculateStatus status) {
        DateTime dateTime = status == CalculateStatus.DAY ?
                DateUtil.offsetDay(DateTime.now(), -1) :
                DateTime.now();
        return create(jsonArray, dateTime);
    }

    public static JobConfig create(JSONArray jsonArray, DateTime dateTime) {
        Dict dict = initDict(dateTime);
        return new JobConfig(dateTime, new TaskIterator(jsonArray), dict);
    }

    public static JobConfig create(File file, String calculateStatus) throws IOException {
        JSONArray jsonArray = JSONUtil.readJSONArray(file, Charset.defaultCharset());
        if (StrUtil.length(calculateStatus) != 10) {
            CalculateStatus status = EnumUtil.fromString(CalculateStatus.class, calculateStatus);
            return create(jsonArray, status);
        } else {
            DateTime dateTime = DateUtil.parse(calculateStatus);
            return create(jsonArray, dateTime);
        }
    }

    public static JobConfig create(String fileName, String calculateStatus) throws IOException {
        return create(FileUtil.file(fileName), calculateStatus);
    }

    public static JobConfig create(String calculateStatus) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("customer.json");
        return create(classPathResource.getFile(), calculateStatus);
    }

    public static JobConfig create(String[] args) throws Exception {
        switch (args.length) {
            case 1:
                return create(args[0]);
            case 2:
                return create(args[0], args[1]);
            default:
                throw new IllegalArgumentException("参数错误: " + StrUtil.join(" ", args));
        }
    }

    private static Dict initDict(DateTime dateTime) {
        return Dict.create()
                .set("BEGIN_OF_DAY", DateUtil.beginOfDay(dateTime).toString(DatePattern.NORM_DATETIME_PATTERN))
                .set("END_OF_DAY", DateUtil.endOfDay(dateTime).toString(DatePattern.NORM_DATETIME_PATTERN))
                .set("DT", dateTime.toDateStr())
                .set("DT_B1", DateUtil.offsetDay(dateTime, -1).toDateStr())
                .set("DT_B7", DateUtil.offsetDay(dateTime, -7).toDateStr())
                .set("DT_B30", DateUtil.offsetDay(dateTime, -30).toDateStr())
                .set("DT_B90", DateUtil.offsetDay(dateTime, -90).toDateStr())
                .set("DT8", dateTime.toString(DatePattern.PURE_DATE_FORMAT));
    }

    public enum CalculateStatus {
        DAY, HOUR, SPEC
    }
}