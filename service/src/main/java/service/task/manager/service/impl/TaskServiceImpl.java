package service.task.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import service.task.manager.dto.task.TaskRequestCreatedDto;
import service.task.manager.dto.task.TaskResponseDto;
import service.task.manager.mapper.TaskMapper;
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
        //repository.save(mapper.toEntity(dto));
    }

    /**
     * @param id
     * @return
     */
    @Override
    public TaskResponseDto findById(Long id) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public List<TaskResponseDto> findAll() {
        return List.of();
    }
}
