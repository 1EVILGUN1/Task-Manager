package service.task.manager.mapper;

import java.time.Duration;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import service.task.manager.dto.subtask.SubtaskRequestCreatedDto;
import service.task.manager.dto.subtask.SubtaskResponseDto;
import service.task.manager.model.Subtask;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-26T20:14:42+0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.2 (Eclipse Adoptium)"
)
@Component
public class SubtaskMapperImpl implements SubtaskMapper {

    @Override
    public Subtask toEntity(SubtaskRequestCreatedDto subtaskRequestCreatedDto) {
        if ( subtaskRequestCreatedDto == null ) {
            return null;
        }

        Subtask subtask = new Subtask();

        subtask.setName( subtaskRequestCreatedDto.name() );
        subtask.setDescription( subtaskRequestCreatedDto.description() );
        subtask.setStartTime( subtaskRequestCreatedDto.startTime() );
        subtask.setDuration( subtaskRequestCreatedDto.duration() );

        return subtask;
    }

    @Override
    public SubtaskResponseDto toResponseDto(Subtask subtask) {
        if ( subtask == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;
        Status status = null;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration duration = null;
        TaskType type = null;

        id = subtask.getId();
        name = subtask.getName();
        description = subtask.getDescription();
        status = subtask.getStatus();
        startTime = subtask.getStartTime();
        endTime = subtask.getEndTime();
        duration = subtask.getDuration();
        type = subtask.getType();

        SubtaskResponseDto subtaskResponseDto = new SubtaskResponseDto( id, name, description, status, startTime, endTime, duration, type );

        return subtaskResponseDto;
    }
}
