package ru.yandex.javacource.golotin.schedule.model;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(String name, Status status, String description,int epicId) {

        super(name, status, description);
        setEpicId(epicId);
    }
    public Subtask(int id,String name,String description,Status status,Integer epicId){
        super(name, status, description);
        setId(id);
        setEpicId(epicId);
    }
    @Override
    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {return TaskType.SUBTASK;}

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return STR."Subtask{epicId=\{epicId}\{'}'}";
    }
}
