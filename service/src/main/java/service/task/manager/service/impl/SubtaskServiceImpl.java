package service.task.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import service.task.manager.dto.subtask.SubtaskRequestCreatedDto;
import service.task.manager.dto.subtask.SubtaskResponseDto;
import service.task.manager.mapper.SubtaskMapper;
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

    }

    /**
     * @param id
     * @return
     */
    @Override
    public SubtaskResponseDto findById(Long id) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public List<SubtaskResponseDto> findAll() {
        return List.of();
    }
}
