package service.task.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import service.task.manager.dto.task.TaskRequestCreatedDto;
import service.task.manager.dto.task.TaskResponseDto;
import service.task.manager.service.TaskService;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/task")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public void create(@RequestBody TaskRequestCreatedDto dto){
        service.create(dto);
    }

    @GetMapping("/{id}")
    public TaskResponseDto get(@PathVariable Long id){
        return service.findById(id);
    }

    @GetMapping
    public List<TaskResponseDto> getAll(){
        return service.findAll();
    }
}
