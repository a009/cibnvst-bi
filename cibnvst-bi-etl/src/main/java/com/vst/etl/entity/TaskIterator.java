package com.vst.etl.entity;

import cn.hutool.json.JSONArray;

import java.util.Iterator;
import java.util.List;

public class TaskIterator implements Iterable<Task>, Iterator<Task> {
    private final List<Task> tasks;
    private int index = 0;

    public TaskIterator(JSONArray jsonArray) {
        this.tasks = jsonArray.toList(Task.class);
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    @Override
    public boolean hasNext() {
        return index < tasks.size();
    }

    @Override
    public Task next() {
        return tasks.get(index++);
    }
}