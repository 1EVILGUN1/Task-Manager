package service.task.manager.dto.subtask;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import service.task.manager.model.enums.Status;

import java.time.Duration;

/**
 * DTO for updating an existing subtask.
 */
@Schema(description = "DTO for updating an existing subtask")
public record SubtaskRequestUpdatedDto(
        @Schema(description = "ID of the subtask to update", example = "1", required = true)
        @NotNull(message = "null id")
        @Positive(message = "not positive id")
        Long id,

        @Schema(description = "Updated name of the subtask", example = "Updated Task 1", required = true)
        @NotBlank(message = "blank name")
        String name,

        @Schema(description = "Updated description of the subtask", example = "Updated first task", required = true)
        @NotBlank(message = "blank description")
        String description,

        @Schema(description = "Updated status of the subtask", example = "IN_PROGRESS", required = true)
        @NotNull(message = "null status")
        Status status,

        @Schema(description = "Updated duration of the subtask", example = "PT48H", required = true)
        @NotNull(message = "null duration")
        Duration duration
) {
}