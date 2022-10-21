package com.vst.etl.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {
    private String name; // 任务名称，方便定位
    private String tableName; // mysql中的表名
    private String dateField; // mysql中的日期字段
    private int[] minusDay; // 环比的天数,如：-1、-7
    private String dorisQuery; // doris的查询
    private String h2Query; // 内存表的查询
    private Boolean disabled; // 是否禁用
    private TaskType taskType; // SIMPLE: 在Doris中INSERT, NORM: 在ETL的逻辑
}