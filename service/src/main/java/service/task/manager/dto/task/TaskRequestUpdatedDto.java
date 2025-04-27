package service.task.manager.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import service.task.manager.model.enums.Status;

import java.time.Duration;

/**
 * DTO for updating an existing task.
 */
@Schema(description = "DTO for updating an existing task")
public record TaskRequestUpdatedDto(
        @Schema(description = "ID of the task to update", example = "1", required = true)
        @NotNull(message = "null id")
        @Positive(message = "not positive id")
        Long id,

        @Schema(description = "Updated name of the task", example = "Updated Task", required = true)
        @NotBlank(message = "blank name")
        String name,

        @Schema(description = "Updated description of the task", example = "Updated standalone task", required = true)
        @NotBlank(message = "blank description")
        String description,

        @Schema(description = "Updated status of the task", example = "IN_PROGRESS", required = true)
        @NotNull(message = "null status")
        Status status,

        @Schema(description = "Updated duration of the task", example = "PT48H", required = true)
        @NotNull(message = "null duration")
        Duration duration
) {
}