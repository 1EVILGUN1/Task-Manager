package service.task.manager.dto.subtask;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO for creating a new subtask.
 */
@Schema(description = "DTO for creating a new subtask")
@Builder
public record SubtaskRequestCreatedDto(
        @Schema(description = "ID of the epic to which the subtask belongs", example = "1", required = true)
        @NotNull(message = "null epic ID")
        Long epicId,

        @Schema(description = "Name of the subtask", example = "Task 1", required = true)
        @NotBlank(message = "blank name")
        String name,

        @Schema(description = "Description of the subtask", example = "First task in the epic", required = true)
        @NotBlank(message = "blank description")
        String description,

        @Schema(description = "Start time of the subtask", example = "2025-04-27T10:00:00", required = true)
        @NotNull(message = "null start time")
        LocalDateTime startTime,

        @Schema(description = "Duration of the subtask", example = "PT24H", required = true)
        @NotNull(message = "null duration")
        Duration duration
) {
}