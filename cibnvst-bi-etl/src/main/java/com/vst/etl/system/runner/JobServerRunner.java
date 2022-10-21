package com.vst.etl.system.runner;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Dict;
import com.vst.etl.config.JobConfig;
import com.vst.etl.entity.Task;
import com.vst.etl.service.TaskService;
import com.vst.etl.service.impl.NormTaskServiceImpl;
import com.vst.etl.service.impl.SimpleTaskServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static com.vst.etl.entity.TaskType.SIMPLE;

/**
 * @author fucheng
 * @date 2022/10/20
 */
@AllArgsConstructor
@Component
@Slf4j
public class JobServerRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        JobConfig jobConfig = JobConfig.create(args);
        log.info("DT: {}", jobConfig.getDateTime().toDateStr());
        for (Task task : jobConfig.getTaskIterator()) {
            if (task.getDisabled()) continue;
            run(task, jobConfig.getDateTime(), jobConfig.getDict());
        }
    }

    private NormTaskServiceImpl normTaskService;
    private SimpleTaskServiceImpl simpleTaskService;


    public void run(Task task, DateTime dateTime, Dict dict) {
        log.info("starting... {}", task.getName());
        TaskService taskService = task.getTaskType() == SIMPLE ? simpleTaskService : normTaskService;
        //TaskService proxyTaskService = ProxyUtil.proxy(taskService, new TimeIntervalAspect());
        taskService.doAction(task, dateTime, dict);
        log.info("finish... {}", task.getName());
    }
}
