package ru.yandex.javacource.golotin.schedule.model;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, Status status, String description, Instant startTime, int duration) {
        super(name, status, description, startTime, duration);
    }

    public Epic(int id, String name, String description, Status status, Instant startTime, int duration) {
        super(name, status, description,startTime, duration);
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

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void setSumDurationSubtasks(Duration duration) {
        setDuration(getDuration().plus(duration).toMinutesPart());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                '}';
    }

}
