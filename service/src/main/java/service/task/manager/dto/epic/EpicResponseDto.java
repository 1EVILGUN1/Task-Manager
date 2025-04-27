package service.task.manager.dto.epic;

import io.swagger.v3.oas.annotations.media.Schema;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for retrieving an epic with its details and subtasks.
 */
@Schema(description = "DTO for retrieving an epic with its details and subtasks")
public record EpicResponseDto(
        @Schema(description = "ID of the epic", example = "1")
        Long id,

        @Schema(description = "List of subtasks associated with the epic")
        List<SubtaskDto> subtasks,

        @Schema(description = "Name of the epic", example = "Project Planning")
        String name,

        @Schema(description = "Description of the epic", example = "Planning phase of the project")
        String description,

        @Schema(description = "Status of the epic", example = "NEW")
        Status status,

        @Schema(description = "Start time of the epic", example = "2025-04-27T10:00:00")
        LocalDateTime startTime,

        @Schema(description = "Duration of the epic", example = "PT24H")
        Duration duration,

        @Schema(description = "End time of the epic", example = "2025-04-28T10:00:00")
        LocalDateTime endTime,

        @Schema(description = "Type of the task (always EPIC)", example = "EPIC")
        TaskType type
) {
    /**
     * DTO for retrieving a subtask within an epic.
     */
    @Schema(description = "DTO for retrieving a subtask within an epic")
    public record SubtaskDto(
            @Schema(description = "ID of the subtask", example = "1")
            Long id,

            @Schema(description = "Name of the subtask", example = "Task 1")
            String name,

            @Schema(description = "Description of the subtask", example = "First task in the epic")
            String description,

            @Schema(description = "Status of the subtask", example = "NEW")
            Status status
    ) {
    }
}