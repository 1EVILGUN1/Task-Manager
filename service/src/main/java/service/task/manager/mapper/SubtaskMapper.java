package service.task.manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import service.task.manager.dto.subtask.SubtaskRequestCreatedDto;
import service.task.manager.dto.subtask.SubtaskRequestUpdatedDto;
import service.task.manager.dto.subtask.SubtaskResponseDto;
import service.task.manager.model.Subtask;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubtaskMapper {

    // Маппинг из DTO в сущность Subtask
    Subtask toEntity(SubtaskRequestCreatedDto dto);

    // Маппинг из сущности Subtask в DTO ответа
    SubtaskResponseDto toResponseDto(Subtask subtask);

    Subtask toEntity(SubtaskRequestUpdatedDto dto);

    Subtask toEntity(SubtaskResponseDto dto);

    @Mapping(target = "id", ignore = true) // Не обновляем ID
    @Mapping(target = "epic", ignore = true)
    @Mapping(target = "startTime", ignore = true) // Оставляем startTime из базы
    @Mapping(target = "endTime", ignore = true)
        // endTime рассчитывается в @PreUpdate
    void updateSubtaskFromDto(SubtaskRequestUpdatedDto dto, @MappingTarget Subtask subtask);
}
