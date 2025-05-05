package service.task.manager.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO for retrieving a task with its details.
 */
@Schema(description = "DTO for retrieving a task with its details")
public record TaskResponseDto(
        @Schema(description = "ID of the task", example = "1")
        Long id,

        @Schema(description = "Name of the task", example = "Standalone Task")
        String name,

        @Schema(description = "Description of the task", example = "A standalone task")
        String description,

        @Schema(description = "Status of the task", example = "NEW")
        Status status,

        @Schema(description = "Start time of the task", example = "2025-04-27T10:00:00")
        LocalDateTime startTime,

        @Schema(description = "End time of the task", example = "2025-04-28T10:00:00")
        LocalDateTime endTime,

        @Schema(description = "Duration of the task", example = "PT24H")
        Duration duration,

        @Schema(description = "Type of the task (always TASK)", example = "TASK")
        TaskType type
) {
}