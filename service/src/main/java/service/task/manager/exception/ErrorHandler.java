package service.task.manager.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(InvalidTaskDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidTaskDataException(InvalidTaskDataException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(TaskConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTaskConstraintViolationException(TaskConstraintViolationException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
            errors.put(fieldName, violation.getMessage());
        }
        return errors;
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (MessageSourceResolvable resolvable : ex.getAllErrors()) {
            if (resolvable instanceof FieldError fieldError) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else if (resolvable instanceof ObjectError objectError) {
                // Для параметров метода (например, @PathVariable)
                String paramName = objectError.getObjectName();
                errors.put(paramName, objectError.getDefaultMessage());
            } else {
                // Для случаев, когда resolvable не является ObjectError
                errors.put("error", resolvable.getDefaultMessage());
            }
        }
        return errors;
    }

    record ErrorResponse(String message) {
    }
}