package service.task.manager.mapper;

import org.mapstruct.*;
import service.task.manager.dto.subtask.SubtaskRequestCreatedDto;
import service.task.manager.dto.subtask.SubtaskResponseDto;
import service.task.manager.model.Epic;
import service.task.manager.model.Subtask;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubtaskMapper {

    // Маппинг из DTO в сущность Subtask
    Subtask toEntity(SubtaskRequestCreatedDto subtaskRequestCreatedDto);

    // Маппинг из сущности Subtask в DTO ответа
    SubtaskResponseDto toResponseDto(Subtask subtask);
}
