package task.manager.schedule.service;

import task.manager.schedule.service.inMemory.InMemoryHistoryManager;

import java.io.File;

public class Manager {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("resources/task.csv"));
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
