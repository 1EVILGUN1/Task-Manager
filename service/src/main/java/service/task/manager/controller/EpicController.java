package service.task.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import service.task.manager.dto.epic.EpicRequestCreatedDto;
import service.task.manager.dto.epic.EpicResponseDto;
import service.task.manager.service.EpicService;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/epic")
public class EpicController {
    private final EpicService service;

    public EpicController(EpicService service) {
        this.service = service;
    }

    @PostMapping
    public void create(@RequestBody EpicRequestCreatedDto dto) {
        service.create(dto);
    }

    @GetMapping("/{id}")
    public EpicResponseDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<EpicResponseDto> findAll() {
        return service.findAll();
    }
}
