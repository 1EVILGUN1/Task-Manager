package ru.yandex.javacource.golotin.schedule.model;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, Status status, String description) {
        super(name, status, description);
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

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                '}';
    }

}
