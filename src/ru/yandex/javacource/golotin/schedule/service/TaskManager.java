package ru.yandex.javacource.golotin.schedule.service;

import ru.yandex.javacource.golotin.schedule.model.Epic;
import ru.yandex.javacource.golotin.schedule.model.Subtask;
import ru.yandex.javacource.golotin.schedule.model.Task;

import java.util.ArrayList;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void cleanTasks();

    void cleanSubtasks();

    void cleanEpics();

    void deleteTask(int id);

    void deleteSubtask(int id);

    void deleteEpic(int id);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    <T> ArrayList<T> getHistory();

}
