package model;

import model.Task;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(String name, Status status, String description) {
        super(name, status, description);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}
