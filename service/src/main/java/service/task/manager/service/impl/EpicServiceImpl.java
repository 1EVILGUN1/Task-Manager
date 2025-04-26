package service.task.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import service.task.manager.dto.epic.EpicRequestCreatedDto;
import service.task.manager.dto.epic.EpicRequestUpdatedDto;
import service.task.manager.dto.epic.EpicResponseDto;
import service.task.manager.error.NotFoundException;
import service.task.manager.mapper.EpicMapper;
import service.task.manager.model.Epic;
import service.task.manager.repository.EpicRepository;
import service.task.manager.service.EpicService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EpicServiceImpl implements EpicService {
    private final EpicRepository repository;
    private final EpicMapper mapper;


    /**
     * @param dto
     */
    @Override
    public void create(EpicRequestCreatedDto dto) {
        repository.save(mapper.toEntity(dto));
    }

    /**
     * @param dto
     * @return
     */
    @Override
    public EpicResponseDto update(EpicRequestUpdatedDto dto) {
        Epic epic = mapper.toEntity(dto);
        epic = repository.save(epic);
        return mapper.toResponseDto(epic);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public EpicResponseDto findById(Long id) {
        return repository.findById(id).
                stream()
                .map(mapper::toResponseDto)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Epic not found"));
    }

    /**
     * @return
     */
    @Override
    public List<EpicResponseDto> findAll() {
        return repository.findAll()
                .stream().map(mapper::toResponseDto)
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
