package service.task.manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import service.task.manager.dto.task.TaskRequestCreatedDto;
import service.task.manager.dto.task.TaskRequestUpdatedDto;
import service.task.manager.dto.task.TaskResponseDto;
import service.task.manager.model.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    // Маппинг из DTO в сущность Task
    Task toEntity(TaskRequestCreatedDto taskRequestCreatedDto);

    // Маппинг из сущности Task в DTO ответа
    TaskResponseDto toResponseDto(Task task);

    Task toEntity(TaskRequestUpdatedDto taskRequestUpdatedDto);

    Task toEntity(TaskResponseDto dto);

    TaskRequestUpdatedDto toTaskRequestUpdatedDto(Task task);

    // Метод для обновления существующей сущности
    @Mapping(target = "id", ignore = true) // Не обновляем ID
    @Mapping(target = "startTime", ignore = true) // Оставляем startTime из базы
    @Mapping(target = "endTime", ignore = true)
    // endTime рассчитывается в @PreUpdate
    void updateTaskFromDto(TaskRequestUpdatedDto dto, @MappingTarget Task task);
}
