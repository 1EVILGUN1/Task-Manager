package service.task.manager.dto.epic;

import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link service.task.manager.model.Epic}
 */
public record EpicResponseDto(Long id, List<SubtaskDto> subtasks, String name, String description, Status status,
                              LocalDateTime startTime, Duration duration, LocalDateTime endTime, TaskType type) {
    /**
     * DTO for {@link service.task.manager.model.Subtask}
     */
    public record SubtaskDto(Long id, String name, String description, Status status) {
    }
}