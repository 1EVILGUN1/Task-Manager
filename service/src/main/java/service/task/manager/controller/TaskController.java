package service.task.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Task API", description = "API for managing tasks")
public class TaskController {
    private final TaskService service;

    @PostMapping
    @Operation(summary = "Create a new task", description = "Creates a new task with the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "409", description = "Task with the same name already exists")
    })
    public ResponseEntity<Void> create(@RequestBody @Valid TaskRequestCreatedDto dto) {
        log.info("Creating task with name: {}", dto.name());
        service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    @Operation(summary = "Update an existing task", description = "Updates an existing task with the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskResponseDto> update(@RequestBody @Valid TaskRequestUpdatedDto dto) {
        log.info("Updating task with ID: {}", dto.id());
        TaskResponseDto updatedTask = service.update(dto);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID", description = "Retrieves a task by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskResponseDto> get(
            @Parameter(description = "ID of the task to retrieve") @PathVariable @Positive(message = "id must be positive")
            @NotNull(message = "null id") Long id) {
        log.info("Fetching task with ID: {}", id);
        TaskResponseDto task = service.findById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves a list of all tasks.")
    @ApiResponse(responseCode = "200", description = "List of tasks retrieved successfully")
    public ResponseEntity<List<TaskResponseDto>> getAll() {
        log.info("Fetching all tasks");
        List<TaskResponseDto> tasks = service.findAll();
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task by ID", description = "Deletes a task by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the task to delete") @PathVariable @Positive(message = "id must be positive")
            @NotNull(message = "null id") Long id) {
        log.info("Deleting task with ID: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}