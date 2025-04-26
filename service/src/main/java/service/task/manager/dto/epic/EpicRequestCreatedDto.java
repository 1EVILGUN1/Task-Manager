package service.task.manager.dto.epic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO for {@link service.task.manager.model.Epic}
 */
@Builder
public record EpicRequestCreatedDto(@NotBlank
                                    String name,
                                    @NotBlank(message = "blank description")
                                    String description,
                                    @NotNull(message = "null start time")
                                    LocalDateTime startTime,
                                    @NotNull(message = "null duration")
                                    Duration duration) {
}