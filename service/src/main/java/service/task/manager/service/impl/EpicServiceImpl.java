package service.task.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.task.manager.dto.epic.EpicRequestCreatedDto;
import service.task.manager.dto.epic.EpicRequestUpdatedDto;
import service.task.manager.dto.epic.EpicResponseDto;
import service.task.manager.error.ConflictException;
import service.task.manager.error.NotFoundException;
import service.task.manager.mapper.EpicMapper;
import service.task.manager.model.Epic;
import service.task.manager.model.enums.Status;
import service.task.manager.repository.EpicRepository;
import service.task.manager.service.EpicService;
import service.task.manager.service.HistoryService;

import java.util.List;

/**
 * Service implementation for managing epics.
 * Provides methods to create, update, retrieve, and delete epics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EpicServiceImpl implements EpicService {
    private final EpicRepository repository;
    private final EpicMapper mapper;
    private final HistoryService history;

    /**
     * Creates a new epic based on the provided DTO.
     * <p>
     * This method checks if an epic with the same name already exists in the database.
     * If it does, a {@link ConflictException} is thrown. Otherwise, the epic is saved
     * with a status of {@link Status#NEW} and the end time calculated based on its start time
     * and duration.
     * </p>
     * @param dto The DTO containing epic creation data (must include name, start time, and duration).
     * @throws ConflictException If an epic with the same name already exists.
     */
    @Transactional
    @Override
    public void create(EpicRequestCreatedDto dto) {
        log.info("Attempting to create epic with name: {}", dto.name());
        if (repository.existsByName(dto.name())) {
            log.warn("Epic creation failed: Epic with name {} already exists", dto.name());
            throw new ConflictException("Epic with name " + dto.name() + " already exists");
        }

        Epic epic = addEndTimeEpicAndStatus(mapper.toEntity(dto));
        repository.save(epic);
        log.info("Epic created successfully with name: {}", dto.name());
    }

    /**
     * Updates an existing epic with the provided data.
     * <p>
     * This method retrieves the epic by its ID, updates its fields using the provided DTO,
     * and saves the changes to the database.
     * </p>
     * @param dto The DTO containing updated epic data (must include epic ID).
     * @return The updated epic as a DTO.
     * @throws NotFoundException If the epic with the specified ID does not exist.
     */
    @Transactional
    @Override
    public EpicResponseDto update(EpicRequestUpdatedDto dto) {
        log.info("Attempting to update epic with ID: {}", dto.id());
        Epic existingEpic = mapper.toEntity(findById(dto.id()));
        mapper.updateTaskFromDto(dto, existingEpic);
        Epic updatedEpic = repository.save(existingEpic);
        log.info("Epic with ID {} updated successfully", updatedEpic.getId());
        return mapper.toResponseDto(updatedEpic);
    }

    /**
     * Retrieves an epic by its ID.
     * @param id The ID of the epic to retrieve.
     * @return The epic as a DTO.
     * @throws NotFoundException If the epic with the specified ID does not exist.
     */
    @Transactional(readOnly = true)
    @Override
    public EpicResponseDto findById(Long id) {
        log.info("Fetching epic with ID: {}", id);
        EpicResponseDto epic = repository.findById(id)
                .stream()
                .map(mapper::toResponseDto)
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Epic with ID {} not found", id);
                    return new NotFoundException("Epic with ID " + id + " not found");
                });
        history.addToHistory(epic.type(), epic.id());
        log.info("Epic with ID {} retrieved successfully", id);
        return epic;
    }

    /**
     * Retrieves all epics.
     * @return A list of all epics as DTOs.
     */
    @Transactional(readOnly = true)
    @Override
    public List<EpicResponseDto> findAll() {
        log.info("Fetching all epics");
        List<EpicResponseDto> epics = repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
        log.info("Retrieved {} epics", epics.size());
        return epics;
    }

    /**
     * Deletes an epic by its ID.
     * <p>
     * This method deletes the epic directly from the database. If the epic does not exist,
     * no exception is thrown as per the current implementation.
     * </p>
     * @param id The ID of the epic to delete.
     */
    @Transactional
    @Override
    public void delete(Long id) {
        log.info("Attempting to delete epic with ID: {}", id);
        repository.deleteById(id);
        log.info("Epic with ID {} deleted successfully", id);
    }

    /**
     * Sets the status to NEW and calculates the end time for the given epic.
     * @param epic The epic to modify.
     * @return The modified epic with updated status and end time.
     */
    private Epic addEndTimeEpicAndStatus(Epic epic) {
        log.debug("Setting status and calculating end time for epic");
        epic.calculateEndTime();
        epic.setStatus(Status.NEW);
        log.debug("Epic status set to NEW and end time calculated");
        return epic;
    }
}