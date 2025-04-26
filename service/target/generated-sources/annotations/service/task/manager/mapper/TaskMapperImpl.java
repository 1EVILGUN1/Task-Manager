package service.task.manager.mapper;

import java.time.Duration;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import service.task.manager.dto.task.TaskRequestCreatedDto;
import service.task.manager.dto.task.TaskResponseDto;
import service.task.manager.model.Task;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-26T20:14:42+0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.2 (Eclipse Adoptium)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public Task toEntity(TaskRequestCreatedDto taskRequestCreatedDto) {
        if ( taskRequestCreatedDto == null ) {
            return null;
        }

        Task task = new Task();

        task.setName( taskRequestCreatedDto.name() );
        task.setDescription( taskRequestCreatedDto.description() );
        task.setStartTime( taskRequestCreatedDto.startTime() );
        task.setDuration( taskRequestCreatedDto.duration() );

        return task;
    }

    @Override
    public TaskResponseDto toResponseDto(Task task) {
        if ( task == null ) {
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

        id = task.getId();
        name = task.getName();
        description = task.getDescription();
        status = task.getStatus();
        startTime = task.getStartTime();
        endTime = task.getEndTime();
        duration = task.getDuration();
        type = task.getType();

        TaskResponseDto taskResponseDto = new TaskResponseDto( id, name, description, status, startTime, endTime, duration, type );

        return taskResponseDto;
    }
}
