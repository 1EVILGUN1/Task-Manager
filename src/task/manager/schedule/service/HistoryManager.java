package task.manager.schedule.service;

import java.util.List;

import task.manager.schedule.model.Task;

public interface HistoryManager {
    void add(Task task);

    List<Task> getAll();

    void remove(int id);


}