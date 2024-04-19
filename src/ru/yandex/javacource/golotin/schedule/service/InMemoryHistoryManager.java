package ru.yandex.javacource.golotin.schedule.service;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.javacource.golotin.schedule.model.Task;
import ru.yandex.javacource.golotin.schedule.service.HistoryManager;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTOY_MAX_SIZE = 10;
    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() >= HISTOY_MAX_SIZE) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}