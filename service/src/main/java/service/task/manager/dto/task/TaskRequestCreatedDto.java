package service.task.manager.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO for creating a new task.
 */
@Schema(description = "DTO for creating a new task")
public record TaskRequestCreatedDto(
        @Schema(description = "Name of the task", example = "Standalone Task", required = true)
        @NotBlank(message = "blank name")
        String name,

        @Schema(description = "Description of the task", example = "A standalone task", required = true)
        @NotBlank(message = "blank description")
        String description,

        @Schema(description = "Start time of the task", example = "2025-04-27T10:00:00", required = true)
        @NotNull(message = "null start time")
        LocalDateTime startTime,

        @Schema(description = "Duration of the task", example = "PT24H", required = true)
        @NotNull(message = "null duration")
        Duration duration
) {
}