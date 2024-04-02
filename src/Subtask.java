public class Subtask extends Task{

    Integer epicId;
    Subtask(String name, Status status, String description) {
        super(name, status, description);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}
