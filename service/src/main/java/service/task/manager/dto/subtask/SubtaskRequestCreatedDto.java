package service.task.manager.dto.subtask;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import service.task.manager.model.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO for {@link Subtask}
 */
@Builder
public record SubtaskRequestCreatedDto(Long epicId,
                                       @NotBlank(message = "blank name") String name,
                                       @NotBlank(message = "blank description") String description,
                                       @NotNull(message = "null start time") LocalDateTime startTime,
                                       @NotNull Duration duration) {
}