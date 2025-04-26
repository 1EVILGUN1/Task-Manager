package service.task.manager.service;

import service.task.manager.dto.epic.EpicRequestCreatedDto;
import service.task.manager.dto.epic.EpicResponseDto;

import java.util.List;

public interface EpicService {
    void create(EpicRequestCreatedDto dto);

    EpicResponseDto findById(Long id);

    List<EpicResponseDto> findAll();
}
