package com.vst.etl.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONArray;
import com.vst.etl.entity.Task;
import com.vst.etl.repository.DorisRepository;
import com.vst.etl.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;

/**
 * 简单处理任务实现接口
 */
@AllArgsConstructor
@Service
public class SimpleTaskServiceImpl implements TaskService {
    private DorisRepository dorisService;

    @Override
    public void doAction(Task task, DateTime dateTime, Dict dict) {
        dorisService.execute(formatSql(task.getDorisQuery(), dict));
    }
}
