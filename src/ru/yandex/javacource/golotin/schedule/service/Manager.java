package ru.yandex.javacource.golotin.schedule.service;

import java.io.File;

public class Manager {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("/Users/admin/Documents/GitHub/java-kanban/src/ru/yandex/javacource/golotin/schedule/resources/task.csv"));
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
