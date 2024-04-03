package ru.yandex.javacource.golotin.schedule.model;

public class Subtask extends Task {

    private Integer epicId = null;

    public Subtask(String name, Status status, String description) {
        super(name, status, description);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                '}';
    }
}
