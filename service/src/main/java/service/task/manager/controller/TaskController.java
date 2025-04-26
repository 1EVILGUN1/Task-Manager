package service.task.manager.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import service.task.manager.dto.task.TaskRequestCreatedDto;
import service.task.manager.dto.task.TaskRequestUpdatedDto;
import service.task.manager.dto.task.TaskResponseDto;
import service.task.manager.service.TaskService;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {
    private final TaskService service;

    @PostMapping
    public void create(@RequestBody @Valid TaskRequestCreatedDto dto) {
        service.create(dto);
    }

    @PutMapping
    public void update(@RequestBody @Valid TaskRequestUpdatedDto dto) {
        service.update(dto);
    }

    @GetMapping("/{id}")
    public TaskResponseDto get(@PathVariable @Positive @NotNull Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<TaskResponseDto> getAll(){
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive @NotNull Long id) {
        service.delete(id);
    }
}
