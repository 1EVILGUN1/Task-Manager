package service.task.manager.mapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import service.task.manager.dto.epic.EpicRequestCreatedDto;
import service.task.manager.dto.epic.EpicResponseDto;
import service.task.manager.model.Epic;
import service.task.manager.model.Subtask;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-26T20:14:42+0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.2 (Eclipse Adoptium)"
)
@Component
public class EpicMapperImpl implements EpicMapper {

    @Override
    public Epic toEntity(EpicRequestCreatedDto epicRequestCreatedDto) {
        if ( epicRequestCreatedDto == null ) {
            return null;
        }

        Epic epic = new Epic();

        epic.setName( epicRequestCreatedDto.name() );
        epic.setDescription( epicRequestCreatedDto.description() );
        epic.setStartTime( epicRequestCreatedDto.startTime() );
        epic.setDuration( epicRequestCreatedDto.duration() );

        return epic;
    }

    @Override
    public EpicResponseDto toResponseDto(Epic epic) {
        if ( epic == null ) {
            return null;
        }

        Long id = null;
        List<EpicResponseDto.SubtaskDto> subtasks = null;
        String name = null;
        String description = null;
        Status status = null;
        LocalDateTime startTime = null;
        Duration duration = null;
        LocalDateTime endTime = null;
        TaskType type = null;

        id = epic.getId();
        subtasks = subtaskListToSubtaskDtoList( epic.getSubtasks() );
        name = epic.getName();
        description = epic.getDescription();
        status = epic.getStatus();
        startTime = epic.getStartTime();
        duration = epic.getDuration();
        endTime = epic.getEndTime();
        type = epic.getType();

        EpicResponseDto epicResponseDto = new EpicResponseDto( id, subtasks, name, description, status, startTime, duration, endTime, type );

        return epicResponseDto;
    }

    @Override
    public EpicResponseDto.SubtaskDto toSubtaskDto(Subtask subtask) {
        if ( subtask == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;
        Status status = null;

        id = subtask.getId();
        name = subtask.getName();
        description = subtask.getDescription();
        status = subtask.getStatus();

        EpicResponseDto.SubtaskDto subtaskDto = new EpicResponseDto.SubtaskDto( id, name, description, status );

        return subtaskDto;
    }

    protected List<EpicResponseDto.SubtaskDto> subtaskListToSubtaskDtoList(List<Subtask> list) {
        if ( list == null ) {
            return null;
        }

        List<EpicResponseDto.SubtaskDto> list1 = new ArrayList<EpicResponseDto.SubtaskDto>( list.size() );
        for ( Subtask subtask : list ) {
            list1.add( toSubtaskDto( subtask ) );
        }

        return list1;
    }
}
