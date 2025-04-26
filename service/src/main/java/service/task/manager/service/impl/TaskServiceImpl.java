package service.task.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import service.task.manager.dto.task.TaskRequestCreatedDto;
import service.task.manager.dto.task.TaskRequestUpdatedDto;
import service.task.manager.dto.task.TaskResponseDto;
import service.task.manager.error.NotFoundException;
import service.task.manager.mapper.TaskMapper;
import service.task.manager.model.Task;
import service.task.manager.repository.TaskRepository;
import service.task.manager.service.TaskService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository repository;
    private final TaskMapper mapper;

    /**
     * @param dto
     */
    @Override
    public void create(TaskRequestCreatedDto dto) {
        repository.save(mapper.toEntity(dto));
    }

    /**
     * @param dto
     * @return
     */
    @Override
    public TaskResponseDto update(TaskRequestUpdatedDto dto) {
        Task task = mapper.toEntity(dto);
        task = repository.save(task);
        return mapper.toResponseDto(task);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public TaskResponseDto findById(Long id) {
        return repository.findById(id).stream()
                .map(mapper::toResponseDto)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Task not found"));
    }

    /**
     * @return
     */
    @Override
    public List<TaskResponseDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    /**
     * @param id
     */
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
