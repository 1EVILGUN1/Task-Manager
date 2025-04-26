package service.task.manager.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import service.task.manager.model.enums.Status;

import java.time.Duration;

/**
 * DTO for {@link service.task.manager.model.Task}
 */
public record TaskRequestUpdatedDto(@NotNull(message = "null id") @Positive(message = "not positive id") Long id,
                                    @NotBlank(message = "blank name") String name,
                                    @NotBlank(message = "blank description") String description,
                                    @NotNull(message = "null status") Status status,
                                    @NotNull(message = "null duration") Duration duration) {
}