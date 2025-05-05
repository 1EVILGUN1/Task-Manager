package service.task.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.task.manager.dto.epic.EpicResponseDto;
import service.task.manager.dto.subtask.SubtaskRequestCreatedDto;
import service.task.manager.dto.subtask.SubtaskRequestUpdatedDto;
import service.task.manager.dto.subtask.SubtaskResponseDto;
import service.task.manager.exception.ConflictException;
import service.task.manager.exception.NotFoundException;
import service.task.manager.mapper.EpicMapper;
import service.task.manager.mapper.SubtaskMapper;
import service.task.manager.model.Epic;
import service.task.manager.model.Subtask;
import service.task.manager.model.enums.Status;
import service.task.manager.repository.SubtaskRepository;
import service.task.manager.service.EpicService;
import service.task.manager.service.HistoryService;
import service.task.manager.service.SubtaskService;

import java.util.Comparator;
import java.util.List;

/**
 * Service implementation for managing subtasks.
 * Provides methods to create, update, retrieve, and delete subtasks associated with epics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubtaskServiceImpl implements SubtaskService {
    private final SubtaskRepository repository;
    private final EpicService epicService;
    private final SubtaskMapper mapper;
    private final EpicMapper epicMapper;
    private final HistoryService history;

    /**
     * Creates a new subtask based on the provided DTO.
     * <p>
     * This method verifies the existence of the associated epic and checks for duplicate subtask names.
     * If the epic does not exist, a {@link NotFoundException} will be thrown by the EpicService.
     * If a subtask with the same name already exists, a {@link ConflictException} is thrown.
     * </p>
     * @param dto The DTO containing subtask creation data (must include name and epic ID).
     * @throws ConflictException If a subtask with the same name already exists.
     * @throws NotFoundException If the associated epic does not exist.
     */
    @Transactional
    @Override
    public void create(SubtaskRequestCreatedDto dto) {
        log.info("Attempting to create subtask with name: {}", dto.name());

        EpicResponseDto epicDto = epicService.findById(dto.epicId());
        Epic epic = epicMapper.toEntity(epicDto);

        if (repository.existsByName(dto.name())) {
            log.warn("Subtask creation failed: Subtask with name {} already exists", dto.name());
            throw new ConflictException("Subtask with name " + dto.name() + " already exists");
        }

        Subtask subtask = addEndTimeSubtaskAndStatus(mapper.toEntity(dto));
        subtask.setEpic(epic);
        repository.save(subtask);
        log.info("Subtask created successfully with name: {}", dto.name());
    }

    /**
     * Updates an existing subtask with the provided data.
     * <p>
     * This method retrieves the subtask by its ID, updates its fields using the provided DTO,
     * and saves the changes to the database.
     * </p>
     * @param dto The DTO containing updated subtask data (must include subtask ID).
     * @return The updated subtask as a DTO.
     * @throws NotFoundException If the subtask with the specified ID does not exist.
     */
    @Transactional
    @Override
    public SubtaskResponseDto update(SubtaskRequestUpdatedDto dto) {
        log.info("Attempting to update subtask with ID: {}", dto.id());

        Subtask existingSubtask = mapper.toEntity(findById(dto.id()));
        mapper.updateSubtaskFromDto(dto, existingSubtask);
        Subtask updatedSubtask = repository.save(existingSubtask);
        log.info("Subtask with ID {} updated successfully", updatedSubtask.getId());
        return mapper.toResponseDto(updatedSubtask);
    }

    /**
     * Retrieves a subtask by its ID.
     * @param id The ID of the subtask to retrieve.
     * @return The subtask as a DTO.
     * @throws NotFoundException If the subtask with the specified ID does not exist.
     */
    @Transactional(readOnly = true)
    @Override
    public SubtaskResponseDto findById(Long id) {
        log.info("Fetching subtask with ID: {}", id);
        SubtaskResponseDto subtask = repository.findById(id).stream()
                .map(mapper::toResponseDto)
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Subtask with ID {} not found", id);
                    return new NotFoundException("Subtask with ID " + id + " not found");
                });
        history.addToHistory(subtask.type(),subtask.id());
        log.info("Subtask with ID {} retrieved successfully", id);
        return subtask;
    }

    /**
     * Retrieves all subtasks.
     * @return A list of all subtasks as DTOs.
     */
    @Transactional(readOnly = true)
    @Override
    public List<SubtaskResponseDto> findAll() {
        log.info("Fetching all subtasks");
        List<SubtaskResponseDto> subtasks = repository.findAll().stream()
                .map(mapper::toResponseDto)
                .toList();
        log.info("Retrieved {} subtasks", subtasks.size());
        return subtasks;
    }

    /**
     * Deletes a subtask by its ID.
     * @param id The ID of the subtask to delete.
     * @throws NotFoundException If the subtask with the specified ID does not exist.
     */
    @Transactional
    @Override
    public void delete(Long id) {
        log.info("Attempting to delete subtask with ID: {}", id);
        findById(id); // Проверяет существование
        repository.deleteById(id);
        log.info("Subtask with ID {} deleted successfully", id);
    }

    /**
     * Retrieves all subtasks sorted by priority and end time.
     * <p>
     * Epics are sorted first by status in the order: IN_PROGRESS, NEW, DONE.
     * Within each status group, subtasks are sorted by end time (earliest first).
     * </p>
     *
     * @return A list of all subtasks as DTOs, sorted by priority and end time.
     */
    @Override
    public List<SubtaskResponseDto> prioritized() {
        log.info("Fetching all subtasks sorted by priority and end time");
        List<SubtaskResponseDto> subtask = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .sorted(Comparator
                        .comparing(this::getStatusPriority)
                        .thenComparing(SubtaskResponseDto::endTime))
                .toList();
        log.info("Retrieved {} prioritized subtasks", subtask.size());
        return subtask;
    }

    /**
     * Helper method to assign priority based on status.
     *
     * @param dto The subtask DTO.
     * @return The priority value (lower means higher priority).
     */
    private int getStatusPriority(SubtaskResponseDto dto) {
        return switch (dto.status()) {
            case IN_PROGRESS -> 1;
            case NEW -> 2;
            case DONE -> 3;
        };
    }

    /**
     * Sets the status to NEW and calculates the end time for the given subtask.
     * @param subtask The subtask to modify.
     * @return The modified subtask with updated status and end time.
     */
    private Subtask addEndTimeSubtaskAndStatus(Subtask subtask) {
        log.debug("Setting status and calculating end time for subtask");
        subtask.calculateEndTime();
        subtask.setStatus(Status.NEW);
        log.debug("Subtask status set to NEW and end time calculated");
        return subtask;
    }
}