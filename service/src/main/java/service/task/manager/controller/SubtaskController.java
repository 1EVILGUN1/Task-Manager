package service.task.manager.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import service.task.manager.dto.subtask.SubtaskRequestCreatedDto;
import service.task.manager.dto.subtask.SubtaskRequestUpdatedDto;
import service.task.manager.dto.subtask.SubtaskResponseDto;
import service.task.manager.service.SubtaskService;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/subtask")
public class SubtaskController {
    private final SubtaskService service;

    @PostMapping
    public void create(@RequestBody @Valid SubtaskRequestCreatedDto dto) {
        service.create(dto);
    }

    @PutMapping
    public void update(@RequestBody @Valid SubtaskRequestUpdatedDto dto) {
        service.update(dto);
    }

    @GetMapping("/{id}")
    public SubtaskResponseDto findById(@PathVariable @Positive @NotNull Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<SubtaskResponseDto> findAll() {
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive @NotNull Long id) {
        service.delete(id);
    }
}
