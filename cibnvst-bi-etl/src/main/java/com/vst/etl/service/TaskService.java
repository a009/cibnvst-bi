package com.vst.etl.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.vst.etl.entity.Task;

public interface TaskService {
    void doAction(Task task, DateTime dateTime, Dict dict);

    default String formatSql(String sql, Dict dict) {
        return StrUtil.format(sql, dict);
    }
}