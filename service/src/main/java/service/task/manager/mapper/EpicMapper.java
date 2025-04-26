package service.task.manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import service.task.manager.dto.subtask.SubtaskRequestUpdatedDto;
import service.task.manager.dto.epic.EpicRequestCreatedDto;
import service.task.manager.dto.epic.EpicResponseDto;
import service.task.manager.model.Epic;
import service.task.manager.dto.epic.EpicRequestUpdatedDto;
import service.task.manager.model.Subtask;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EpicMapper {

    // Маппинг из DTO в сущность Epic
    Epic toEntity(EpicRequestCreatedDto epicRequestCreatedDto);

    // Маппинг из сущности Epic в DTO ответа
    EpicResponseDto toResponseDto(Epic epic);

    // Маппинг для подзадач (Subtask -> SubtaskDto)
    EpicResponseDto.SubtaskDto toSubtaskDto(Subtask subtask);

    Subtask toEntity(SubtaskRequestUpdatedDto subtaskRequestUpdatedDto);

    SubtaskRequestUpdatedDto toSubtaskRequestUpdatedDto(Subtask subtask);

    Epic toEntity(EpicRequestUpdatedDto epicRequestUpdatedDto);

    EpicRequestUpdatedDto toEpicDto(Epic epic);
}
