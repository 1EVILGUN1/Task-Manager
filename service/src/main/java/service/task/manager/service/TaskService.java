package service.task.manager.service;

import service.task.manager.dto.task.TaskRequestCreatedDto;
import service.task.manager.dto.task.TaskRequestUpdatedDto;
import service.task.manager.dto.task.TaskResponseDto;

import java.util.List;

public interface TaskService {
    void create(TaskRequestCreatedDto dto);

    TaskResponseDto update(TaskRequestUpdatedDto dto);

    TaskResponseDto findById(Long id);

    List<TaskResponseDto> findAll();

    void delete(Long id);
}
