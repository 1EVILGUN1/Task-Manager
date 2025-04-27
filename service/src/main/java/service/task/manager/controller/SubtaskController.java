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
@Tag(name = "Subtask API", description = "API for managing subtasks")
public class SubtaskController {
    private final SubtaskService service;

    @PostMapping
    @Operation(summary = "Create a new subtask", description = "Creates a new subtask with the provided data, associated with an epic.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subtask created successfully"),
            @ApiResponse(responseCode = "404", description = "Associated epic not found"),
            @ApiResponse(responseCode = "409", description = "Subtask with the same name already exists")
    })
    public ResponseEntity<Void> create(@RequestBody @Valid SubtaskRequestCreatedDto dto) {
        log.info("Creating subtask with name: {}", dto.name());
        service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    @Operation(summary = "Update an existing subtask", description = "Updates an existing subtask with the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subtask updated successfully"),
            @ApiResponse(responseCode = "404", description = "Subtask not found")
    })
    public ResponseEntity<SubtaskResponseDto> update(@RequestBody @Valid SubtaskRequestUpdatedDto dto) {
        log.info("Updating subtask with ID: {}", dto.id());
        SubtaskResponseDto updatedSubtask = service.update(dto);
        return ResponseEntity.ok(updatedSubtask);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a subtask by ID", description = "Retrieves a subtask by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subtask retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Subtask not found")
    })
    public ResponseEntity<SubtaskResponseDto> findById(
            @Parameter(description = "ID of the subtask to retrieve") @PathVariable @Positive @NotNull Long id) {
        log.info("Fetching subtask with ID: {}", id);
        SubtaskResponseDto subtask = service.findById(id);
        return ResponseEntity.ok(subtask);
    }

    @GetMapping
    @Operation(summary = "Get all subtasks", description = "Retrieves a list of all subtasks.")
    @ApiResponse(responseCode = "200", description = "List of subtasks retrieved successfully")
    public ResponseEntity<List<SubtaskResponseDto>> findAll() {
        log.info("Fetching all subtasks");
        List<SubtaskResponseDto> subtasks = service.findAll();
        return ResponseEntity.ok(subtasks);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a subtask by ID", description = "Deletes a subtask by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subtask deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subtask not found")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the subtask to delete") @PathVariable @Positive @NotNull Long id) {
        log.info("Deleting subtask with ID: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}