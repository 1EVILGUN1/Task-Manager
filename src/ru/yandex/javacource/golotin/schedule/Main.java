package ru.yandex.javacource.golotin.schedule;

import ru.yandex.javacource.golotin.schedule.model.Epic;
import ru.yandex.javacource.golotin.schedule.model.Status;
import ru.yandex.javacource.golotin.schedule.model.Subtask;
import ru.yandex.javacource.golotin.schedule.model.Task;

import ru.yandex.javacource.golotin.schedule.service.Manager;
import ru.yandex.javacource.golotin.schedule.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Manager.getDefaults();
        taskManager.createTask(new Task("Дом", Status.NEW, "Убраться в кухни и ванной"));
        taskManager.createTask(new Task("Работа", Status.IN_PROGRESS, "Сделать куча рутины и пойти домой:)"));

        taskManager.createEpic(new Epic("Прогулка", Status.NEW, "Прежде чем погулять нужно:"));
        taskManager.createSubtask(new Subtask("Уборка", Status.NEW, "Убраться в квартире", 1));
        taskManager.createSubtask(new Subtask("Одежда", Status.NEW, "Подготовить одежду к прогулке", 1));

        taskManager.createEpic(new Epic("Приготовить кофе", Status.NEW, "Пойти на кухню и:"));
        taskManager.createSubtask(new Subtask("Сделать кофе", Status.NEW, "Налить в кружку горячую воду и наспать кофе", 2));

        taskManager.updateTask(new Task("Дом", Status.IN_PROGRESS, "Уборка в кухни и ванной"));

        taskManager.updateEpic(new Epic("Прогулка", Status.NEW, "Не пойду гулять"));
        taskManager.updateSubtask(new Subtask("Уборка", Status.IN_PROGRESS, "Убираюсь)", 1));
        taskManager.updateSubtask(new Subtask("Сделать кофе", Status.DONE, "Кофе приготовлено", 1));


        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());

        taskManager.deleteTask(1);
        taskManager.deleteEpic(1);
        taskManager.deleteSubtask(1);

        taskManager.cleanTasks();
        taskManager.cleanSubtasks();
        taskManager.cleanEpics();

    }
}