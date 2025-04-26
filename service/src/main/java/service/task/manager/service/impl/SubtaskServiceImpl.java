package service.task.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import service.task.manager.dto.subtask.SubtaskRequestCreatedDto;
import service.task.manager.dto.subtask.SubtaskRequestUpdatedDto;
import service.task.manager.dto.subtask.SubtaskResponseDto;
import service.task.manager.error.NotFoundException;
import service.task.manager.mapper.SubtaskMapper;
import service.task.manager.model.Epic;
import service.task.manager.model.Subtask;
import service.task.manager.repository.SubtaskRepository;
import service.task.manager.service.SubtaskService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubtaskServiceImpl implements SubtaskService {
    private final SubtaskRepository repository;
    private final SubtaskMapper mapper;



    /**
     * @param dto
     */
    @Override
    public void create(SubtaskRequestCreatedDto dto) {
        repository.save(mapper.toEntity(dto));
    }

    /**
     * @param dto
     * @return
     */
    @Override
    public SubtaskResponseDto update(SubtaskRequestUpdatedDto dto) {
        Subtask subtask = mapper.toEntity(dto);
        subtask = repository.save(subtask);
        return mapper.toResponseDto(subtask);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public SubtaskResponseDto findById(Long id) {
        return repository.findById(id).stream()
                .map(mapper::toResponseDto)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Subtask not found"));
    }

    /**
     * @return
     */
    @Override
    public List<SubtaskResponseDto> findAll() {
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
