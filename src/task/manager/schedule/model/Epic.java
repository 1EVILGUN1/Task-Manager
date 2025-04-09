package task.manager.schedule.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, Status status, String description, LocalDateTime startTime, long duration) {
        super(name, status, description, startTime, duration);
    }

    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, long duration) {
        super(name, status, description, startTime, duration);
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", endTime=" + endTime +
                '}';
    }
}
