package ru.yandex.javacource.golotin.schedule.converter;

import ru.yandex.javacource.golotin.schedule.model.Task;
import ru.yandex.javacource.golotin.schedule.model.TaskType;

public class TaskConverter {
    public TaskType getType() {
        return TaskType.TASK;
    }

    public String toString(Task task) {
        return task.getId() + "," + task.getName() + "," + task.getDescription();
    }
}
