package ru.yandex.javacource.golotin.schedule.service;

import java.util.List;

import ru.yandex.javacource.golotin.schedule.model.Task;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();


}