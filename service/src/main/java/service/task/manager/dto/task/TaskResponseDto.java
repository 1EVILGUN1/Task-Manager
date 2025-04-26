package service.task.manager.dto.task;

import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO for {@link service.task.manager.model.Task}
 */
public record TaskResponseDto(Long id, String name, String description, Status status, LocalDateTime startTime,
                              LocalDateTime endTime, Duration duration, TaskType type) {
}