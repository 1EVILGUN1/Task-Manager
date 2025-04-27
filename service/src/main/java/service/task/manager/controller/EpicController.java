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
@Tag(name = "Epic API", description = "API for managing epics")
public class EpicController {
    private final EpicService service;

    @PostMapping
    @Operation(summary = "Create a new epic", description = "Creates a new epic with the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Epic created successfully"),
            @ApiResponse(responseCode = "409", description = "Epic with the same name already exists")
    })
    public ResponseEntity<Void> create(@RequestBody @Valid EpicRequestCreatedDto dto) {
        log.info("Creating epic with name: {}", dto.name());
        service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    @Operation(summary = "Update an existing epic", description = "Updates an epic with the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Epic updated successfully"),
            @ApiResponse(responseCode = "404", description = "Epic not found")
    })
    public ResponseEntity<EpicResponseDto> update(@RequestBody @Valid EpicRequestUpdatedDto dto) {
        log.info("Updating epic with ID: {}", dto.id());
        EpicResponseDto updatedEpic = service.update(dto);
        return ResponseEntity.ok(updatedEpic);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an epic by ID", description = "Retrieves an epic by its ID, including its subtasks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Epic retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Epic not found")
    })
    public ResponseEntity<EpicResponseDto> findById(
            @Parameter(description = "ID of the epic to retrieve") @PathVariable @Positive @NotNull Long id) {
        log.info("Fetching epic with ID: {}", id);
        EpicResponseDto epic = service.findById(id);
        return ResponseEntity.ok(epic);
    }

    @GetMapping
    @Operation(summary = "Get all epics", description = "Retrieves a list of all epics.")
    @ApiResponse(responseCode = "200", description = "List of epics retrieved successfully")
    public ResponseEntity<List<EpicResponseDto>> findAll() {
        log.info("Fetching all epics");
        List<EpicResponseDto> epics = service.findAll();
        return ResponseEntity.ok(epics);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an epic by ID", description = "Deletes an epic by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Epic deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Epic not found")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the epic to delete") @PathVariable @Positive @NotNull Long id) {
        log.info("Deleting epic with ID: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}