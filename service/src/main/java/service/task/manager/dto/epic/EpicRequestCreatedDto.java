package service.task.manager.dto.epic;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO for creating a new epic.
 */
@Schema(description = "DTO for creating a new epic")
@Builder
public record EpicRequestCreatedDto(
        @Schema(description = "Name of the epic", example = "Project Planning", required = true)
        @NotBlank(message = "blank name")
        String name,

        @Schema(description = "Description of the epic", example = "Planning phase of the project", required = true)
        @NotBlank(message = "blank description")
        String description,

        @Schema(description = "Start time of the epic", example = "2025-04-27T10:00:00", required = true)
        @NotNull(message = "null start time")
        LocalDateTime startTime,

        @Schema(description = "Duration of the epic", example = "PT24H", required = true)
        @NotNull(message = "null duration")
        Duration duration
) {
}