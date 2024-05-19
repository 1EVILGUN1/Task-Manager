package ru.yandex.javacource.golotin.schedule.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, Status status, String description) {
        super(name, status, description);
    }

    public Epic(int id,String name,String description,Status status){
        super(name, status, description);
        setId(id);
    }
    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void cleanSubtask() {
        subtaskIds.clear();
    }

    public void removeSubtask(int id) {
        subtaskIds.remove(id);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public TaskType getType() {return TaskType.EPIC;}

    @Override
    public String toString() {
        return STR."Epic{subtaskIds=\{subtaskIds}\{'}'}";
    }

}
