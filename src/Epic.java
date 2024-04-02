import java.util.ArrayList;

public class Epic extends Task{

    ArrayList<Subtask> subtasks = new ArrayList<>();
    Epic(String name, Status status, String description) {
        super(name, status, description);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {

        this.subtasks = subtasks;
    }

}
