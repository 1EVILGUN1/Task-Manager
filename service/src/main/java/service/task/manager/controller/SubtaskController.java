package service.task.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import service.task.manager.dto.subtask.SubtaskRequestCreatedDto;
import service.task.manager.dto.subtask.SubtaskResponseDto;
import service.task.manager.service.SubtaskService;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/subtask")
public class SubtaskController {
    private final SubtaskService service;

    public SubtaskController(SubtaskService service) {
        this.service = service;
    }

    @PostMapping
    public void create(@RequestBody SubtaskRequestCreatedDto dto) {
        service.create(dto);
    }

    @GetMapping("/{id}")
    public SubtaskResponseDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<SubtaskResponseDto> findAll() {
        return service.findAll();
    }
}
