package service.task.manager.dto.subtask;

import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO for {@link service.task.manager.model.Subtask}
 */
public record SubtaskResponseDto(Long id, String name, String description, Status status, LocalDateTime startTime,
                                 LocalDateTime endTime, Duration duration, TaskType type) {
}