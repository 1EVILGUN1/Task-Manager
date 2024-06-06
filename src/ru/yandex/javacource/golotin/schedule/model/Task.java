package ru.yandex.javacource.golotin.schedule.model;

import java.time.Duration;
import java.util.Objects;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;

    private Instant startTime; // LocalDateTime
    private Duration duration; // минуты или Duration
    private Instant endTime;

    public Task(String name, Status status, String description, Instant startTime, int duration) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
        this.endTime = startTime.plus(duration, ChronoUnit.MINUTES);
    }

    public Task(int id, String name, String description, Status status, Instant startTime, int duration) {
        setId(id);
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
        this.endTime = startTime.plus(duration, ChronoUnit.MINUTES);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Integer getEpicId() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }


}
