package ru.yandex.javacource.golotin.schedule.service;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.javacource.golotin.schedule.model.Task;
import ru.yandex.javacource.golotin.schedule.service.HistoryManager;

public class InMemoryHistoryManager implements HistoryManager {
    private final int ARRAY_SIZE = 10;
    List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() < ARRAY_SIZE) {

            history.add(task);
        } else {
            history.remove(0);
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}