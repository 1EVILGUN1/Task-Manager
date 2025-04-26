package service.task.manager.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import service.task.manager.dto.epic.EpicRequestCreatedDto;
import service.task.manager.dto.epic.EpicRequestUpdatedDto;
import service.task.manager.dto.epic.EpicResponseDto;
import service.task.manager.service.EpicService;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/epic")
public class EpicController {
    private final EpicService service;

    @PostMapping
    public void create(@RequestBody @Valid EpicRequestCreatedDto dto) {
        service.create(dto);
    }

    @PutMapping
    public EpicResponseDto update(@RequestBody @Valid EpicRequestUpdatedDto dto) {
        return service.update(dto);
    }

    @GetMapping("/{id}")
    public EpicResponseDto findById(@PathVariable @Positive @NotNull Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<EpicResponseDto> findAll() {
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive @NotNull Long id) {
        service.delete(id);
    }
}
