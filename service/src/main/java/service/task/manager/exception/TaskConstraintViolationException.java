package service.task.manager.exception;

public class TaskConstraintViolationException extends RuntimeException {
    public TaskConstraintViolationException(String message) {
        super(message);
    }
}