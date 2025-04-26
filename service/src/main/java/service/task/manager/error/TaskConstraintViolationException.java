package service.task.manager.error;

public class TaskConstraintViolationException extends RuntimeException {
    public TaskConstraintViolationException(String message) {
        super(message);
    }
}