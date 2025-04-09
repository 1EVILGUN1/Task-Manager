package ru.yandex.javacource.golotin.schedule.test.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.golotin.schedule.exception.ManagerSaveException;
import ru.yandex.javacource.golotin.schedule.model.Status;
import ru.yandex.javacource.golotin.schedule.model.Task;
import ru.yandex.javacource.golotin.schedule.service.InMemoryTaskManager;
import ru.yandex.javacource.golotin.schedule.service.Manager;
import ru.yandex.javacource.golotin.schedule.service.TaskManager;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Manager.getDefault();
    }


    @Test
    void shouldCreateTask() {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        Task task = new Task("test", Status.NEW, "testing", localDateTime,30);
        Task task2 = new Task("test2", Status.NEW, "testing2",LocalDateTime.now(),45);
        Task task3 = task;
        assertEquals(task, task2);
        assertSame(task, task3);

        Task result = taskManager.createTask(task);

        assertNotNull(result);
        Task clone = taskManager.getTask(result.getId());
        assertEquals(clone.getId(), result.getId());
        assertEquals(clone.getName(), result.getName());
        assertTrue(taskManager.getTasks().contains(task));

    }

    @Test
    void shouldUpdateTask() {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        Task task = new Task("test", Status.NEW, "testing", localDateTime,30);
        taskManager.createTask(task);
        Task task2 = new Task(0,"test2", "testing2", Status.NEW,  LocalDateTime.now(),45);
        taskManager.updateTask(task2);
        assertEquals(task, taskManager.getTask(1));
    }

    @Test
    void shouldDeleteTask() {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        Task task = new Task("test", Status.NEW, "testing", localDateTime,30);
        Task task2 = new Task("test2", Status.NEW, "testing2", LocalDateTime.now(),45);
        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.deleteTask(1);
        Exception exception = assertThrows(ManagerSaveException.class, ()->taskManager.getTask(1));
        String expectedMessage = "Задача с ид=" + 1;
                String actualMessage = exception.getMessage();
                assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void shouldCleanTask() {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        Task task = new Task("test", Status.NEW, "testing", LocalDateTime.now(),30);
        Task task2 = new Task("test2", Status.NEW, "testing2", localDateTime,45);
        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.cleanTasks();
        assertEquals(taskManager.getTasks(), taskManager.getEpics());
    }

    @Test
    void shouldGetTasks() {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        Task task = new Task("test", Status.NEW, "testing", localDateTime,30);
        Task task2 = new Task("test2", Status.NEW, "testing2", LocalDateTime.now(),45);
        taskManager.createTask(task);
        taskManager.createTask(task2);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(task2);
        assertEquals(tasks, taskManager.getTasks());
    }

    @Test
    void shouldaddPriorityTask(){
        LocalDateTime localDateTime = LocalDateTime.now();
        Task task = new Task("test", Status.NEW, "testing", localDateTime,30);
        Task task2 = new Task("test2", Status.NEW, "testing2", localDateTime,45);
        taskManager.createTask(task);
        Exception exception = assertThrows(ManagerSaveException.class, () -> taskManager.createTask(task2));

        String expectedMessage = "Задача пересекаются с id=" + task.getId() + " c " + task.getStartTime() + " по " + task.getEndTime();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

    }
}