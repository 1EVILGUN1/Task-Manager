package service.task.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.task.manager.dto.task.TaskRequestCreatedDto;
import service.task.manager.dto.task.TaskRequestUpdatedDto;
import service.task.manager.dto.task.TaskResponseDto;
import service.task.manager.exception.ConflictException;
import service.task.manager.exception.NotFoundException;
import service.task.manager.mapper.TaskMapper;
import service.task.manager.model.Task;
import service.task.manager.model.enums.Status;
import service.task.manager.repository.TaskRepository;
import service.task.manager.service.HistoryService;
import service.task.manager.service.TaskService;

import java.util.Comparator;
import java.util.List;

/**
 * Service implementation for managing tasks.
 * Provides methods to create, update, retrieve, and delete tasks.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final HistoryService history;

    /**
     * Creates a new task based on the provided DTO.
     * @param dto The DTO containing task creation data.
     * @throws ConflictException If a task with the same name already exists.
     */
    @Transactional
    @Override
    public void create(TaskRequestCreatedDto dto) {
        log.info("Attempting to create task with name: {}", dto.name());
        if (repository.existsByName(dto.name())) {
            log.warn("Task creation failed: Task with name {} already exists", dto.name());
            throw new ConflictException("Task with name " + dto.name() + " already exists");
        }

        Task task = addEndTimeTaskAndStatus(mapper.toEntity(dto));
        repository.save(task);
        log.info("Task created successfully with name: {}", dto.name());
    }

    /**
     * Updates an existing task with the provided data.
     * @param dto The DTO containing updated task data.
     * @return The updated task as a DTO.
     * @throws NotFoundException If the task with the specified ID does not exist.
     */
    @Transactional
    @Override
    public TaskResponseDto update(TaskRequestUpdatedDto dto) {
        log.info("Attempting to update task with ID: {}", dto.id());
        Task existingTask = mapper.toEntity(findById(dto.id()));
        mapper.updateTaskFromDto(dto, existingTask);
        Task updatedTask = repository.save(existingTask);
        log.info("Task with ID {} updated successfully", updatedTask.getId());
        return mapper.toResponseDto(updatedTask);
    }

    /**
     * Retrieves a task by its ID.
     * @param id The ID of the task to retrieve.
     * @return The task as a DTO.
     * @throws NotFoundException If the task with the specified ID does not exist.
     */
    @Transactional(readOnly = true)
    @Override
    public TaskResponseDto findById(Long id) {
        log.info("Fetching task with ID: {}", id);
        TaskResponseDto task = repository.findById(id).stream()
                .map(mapper::toResponseDto)
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Task with ID {} not found", id);
                    return new NotFoundException("Task with ID " + id + " not found");
                });
        history.addToHistory(task.type(),task.id());
        log.info("Task with ID {} retrieved successfully", id);
        return task;
    }

    /**
     * Retrieves all tasks.
     * @return A list of all tasks as DTOs.
     */
    @Transactional(readOnly = true)
    @Override
    public List<TaskResponseDto> findAll() {
        log.info("Fetching all tasks");
        List<TaskResponseDto> tasks = repository.findAll().stream()
                .map(mapper::toResponseDto)
                .toList();
        log.info("Retrieved {} tasks", tasks.size());
        return tasks;
    }

    /**
     * Deletes a task by its ID.
     * @param id The ID of the task to delete.
     * @throws NotFoundException If the task with the specified ID does not exist.
     */
    @Transactional
    @Override
    public void delete(Long id) {
        log.info("Attempting to delete task with ID: {}", id);
        findById(id); // Проверяет существование
        repository.deleteById(id);
        log.info("Task with ID {} deleted successfully", id);
    }

    /**
     * Retrieves all tasks sorted by priority and end time.
     * <p>
     * Epics are sorted first by status in the order: IN_PROGRESS, NEW, DONE.
     * Within each status group, tasks are sorted by end time (earliest first).
     * </p>
     *
     * @return A list of all tasks as DTOs, sorted by priority and end time.
     */
    @Override
    public List<TaskResponseDto> prioritized() {
        log.info("Fetching all subtasks sorted by priority and end time");
        List<TaskResponseDto> tasks = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .sorted(Comparator
                        .comparing(this::getStatusPriority)
                        .thenComparing(TaskResponseDto::endTime))
                .toList();
        log.info("Retrieved {} prioritized subtasks", tasks.size());
        return tasks;
    }

    /**
     * Helper method to assign priority based on status.
     *
     * @param dto The task DTO.
     * @return The priority value (lower means higher priority).
     */
    private int getStatusPriority(TaskResponseDto dto) {
        return switch (dto.status()) {
            case IN_PROGRESS -> 1;
            case NEW -> 2;
            case DONE -> 3;
        };
    }

    /**
     * Sets the status to NEW and calculates the end time for the given task.
     * @param task The task to modify.
     * @return The modified task with updated status and end time.
     */
    private Task addEndTimeTaskAndStatus(Task task) {
        log.debug("Setting status and calculating end time for task");
        task.calculateEndTime();
        task.setStatus(Status.NEW);
        log.debug("Task status set to NEW and end time calculated");
        return task;
    }
}