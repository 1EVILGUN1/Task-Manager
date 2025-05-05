package service.task.manager.service;

import service.task.manager.dto.subtask.SubtaskRequestCreatedDto;
import service.task.manager.dto.subtask.SubtaskRequestUpdatedDto;
import service.task.manager.dto.subtask.SubtaskResponseDto;

import java.util.List;

public interface SubtaskService {
    void create(SubtaskRequestCreatedDto dto);

    SubtaskResponseDto update(SubtaskRequestUpdatedDto dto);

    SubtaskResponseDto findById(Long id);

    List<SubtaskResponseDto> findAll();

    void delete(Long id);

    List<SubtaskResponseDto> prioritized();
}
