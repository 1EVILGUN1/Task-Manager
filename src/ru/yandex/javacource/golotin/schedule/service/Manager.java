package ru.yandex.javacource.golotin.schedule.service;

public class Manager {
    public static TaskManager getDefaults(){
        return new InMemoryTaskManager();
    }
}