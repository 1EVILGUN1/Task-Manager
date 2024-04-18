package ru.yandex.javacource.golotin.schedule.test.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.golotin.schedule.model.Status;
import ru.yandex.javacource.golotin.schedule.model.Task;
import ru.yandex.javacource.golotin.schedule.service.InMemoryTaskManager;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }


    @Test
    void shouldCreateTask() {
        Task task = new Task("test", Status.NEW, "testing");
        Task task2 = new Task("test2", Status.NEW, "testing2");
        Task task3 = task;
        assertEquals(task, task2);
        assertSame(task, task3);

        Task result = taskManager.createTask(task);

        assertNotNull(result);
        Task clone = taskManager.getTask(result.getId());
        assertEquals(clone.getId(), result.getId());
        assertEquals(clone.getName(), result.getName());
        assertTrue(taskManager.getTask().containsValue(task));

    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("test", Status.NEW, "testing");
        taskManager.createTask(task);
        Task task2 = new Task("test2", Status.NEW, "testing2");
        taskManager.updateTask(task2);
        assertNotEquals(task, taskManager.getTask(1));
    }

    @Test
    void shouldDeleteTask() {
        Task task = new Task("test", Status.NEW, "testing");
        Task task2 = new Task("test2", Status.NEW, "testing2");
        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.deleteTask(1);
        assertNull(taskManager.getTask(1));
    }

    @Test
    void shouldCleanTask() {
        Task task = new Task("test", Status.NEW, "testing");
        Task task2 = new Task("test2", Status.NEW, "testing2");
        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.cleanTasks();
        assertEquals(taskManager.getTask(), taskManager.getEpic());
    }

    @Test
    void shouldGetTasks() {
        Task task = new Task("test", Status.NEW, "testing");
        Task task2 = new Task("test2", Status.NEW, "testing2");
        taskManager.createTask(task);
        taskManager.createTask(task2);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(task2);
        assertEquals(tasks, taskManager.getTasks());

    }
}