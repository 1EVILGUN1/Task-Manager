package service.task.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import service.task.manager.dto.epic.EpicRequestCreatedDto;
import service.task.manager.dto.epic.EpicResponseDto;
import service.task.manager.mapper.EpicMapper;
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

    }

    /**
     * @param id
     * @return
     */
    @Override
    public EpicResponseDto findById(Long id) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public List<EpicResponseDto> findAll() {
        return List.of();
    }
}
