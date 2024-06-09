package ru.yandex.javacource.golotin.schedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.golotin.schedule.model.Status;
import ru.yandex.javacource.golotin.schedule.model.Task;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    TaskManager taskManager;
    @BeforeEach
    void beforeEach() {
        taskManager = Manager.getDefault();

    }

    @Test
    void createTask() {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        Task task = new Task("test", Status.NEW, "testing", localDateTime,30);
        Task task2 = new Task("test2", Status.NEW, "testing2",LocalDateTime.now(),45);
        taskManager.createTask(task);
        taskManager.createTask(task2);
        assertEquals(taskManager.getTasks(),FileBackedTaskManager.loadFromFile(new File("resources/task.csv")).getTasks());
    }

    @Test
    void updateTask() {
        Task task = new Task("test", Status.NEW, "testing", LocalDateTime.now(),30);
        taskManager.createTask(task);
        Task task2 = new Task(0,"test2","testing2", Status.NEW, LocalDateTime.now(),45);
        taskManager.updateTask(task2);
        assertEquals(task, FileBackedTaskManager.loadFromFile(new File("resources/task.csv")).getTask(1));
    }

    @Test
    void deleteTask() {
        Task task = new Task("test", Status.NEW, "testing", LocalDateTime.now(),30);
        taskManager.createTask(task);
        TaskManager taskManager1 = taskManager;
        taskManager.deleteTask(task.getId());
        assertNotEquals(taskManager, taskManager1);
    }

}