package service.task.manager.service;

import service.task.manager.dto.subtask.SubtaskRequestCreatedDto;
import service.task.manager.dto.subtask.SubtaskResponseDto;
import service.task.manager.model.Subtask;

import java.util.List;

public interface SubtaskService {
    void create(SubtaskRequestCreatedDto dto);

    SubtaskResponseDto findById(Long id);

    List<SubtaskResponseDto> findAll();
}
