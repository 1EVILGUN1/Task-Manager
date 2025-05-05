package service.task.manager.dto.subtask;

import io.swagger.v3.oas.annotations.media.Schema;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO for retrieving a subtask with its details.
 */
@Schema(description = "DTO for retrieving a subtask with its details")
public record SubtaskResponseDto(
        @Schema(description = "ID of the subtask", example = "1")
        Long id,

        @Schema(description = "ID of the epic to which the subtask belongs", example = "1")
        Long epicId,

        @Schema(description = "Name of the subtask", example = "Task 1")
        String name,

        @Schema(description = "Description of the subtask", example = "First task in the epic")
        String description,

        @Schema(description = "Status of the subtask", example = "NEW")
        Status status,

        @Schema(description = "Start time of the subtask", example = "2025-04-27T10:00:00")
        LocalDateTime startTime,

        @Schema(description = "End time of the subtask", example = "2025-04-28T10:00:00")
        LocalDateTime endTime,

        @Schema(description = "Duration of the subtask", example = "PT24H")
        Duration duration,

        @Schema(description = "Type of the task (always SUBTASK)", example = "SUBTASK")
        TaskType type
) {
}