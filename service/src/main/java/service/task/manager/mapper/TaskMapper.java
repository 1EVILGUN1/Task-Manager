package service.task.manager.mapper;

import org.mapstruct.Mapper;
import service.task.manager.dto.task.TaskRequestUpdatedDto;
import service.task.manager.dto.task.TaskRequestCreatedDto;
import service.task.manager.dto.task.TaskResponseDto;
import service.task.manager.model.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    // Маппинг из DTO в сущность Task
    Task toEntity(TaskRequestCreatedDto taskRequestCreatedDto);

    // Маппинг из сущности Task в DTO ответа
    TaskResponseDto toResponseDto(Task task);

    Task toEntity(TaskRequestUpdatedDto taskRequestUpdatedDto);

    TaskRequestUpdatedDto toTaskRequestUpdatedDto(Task task);
}
