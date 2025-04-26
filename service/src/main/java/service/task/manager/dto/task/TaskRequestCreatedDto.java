package service.task.manager.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO for {@link service.task.manager.model.Task}
 */
public record TaskRequestCreatedDto(@NotBlank(message = "blank name") String name,
                                    @NotBlank(message = "blank description") String description,
                                    @NotNull(message = "null start time ") LocalDateTime startTime,
                                    @NotNull Duration duration) {
}