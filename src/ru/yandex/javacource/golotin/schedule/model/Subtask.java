package ru.yandex.javacource.golotin.schedule.model;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(String name, Status status, String description,int epicId) {

        super(name, status, description);
        setEpicId(epicId);
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
