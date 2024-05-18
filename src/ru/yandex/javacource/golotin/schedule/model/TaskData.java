package ru.yandex.javacource.golotin.schedule.model;

import java.util.List;

public class TaskData {
    final List<Task> tasks;
    final List<Integer> history;

    public TaskData(List<Task> tasks, List<Integer> history) {
        this.tasks = tasks;
        this.history = history;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Integer> getHistory() {
        return history;
    }
}
