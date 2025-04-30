package service.task.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.task.manager.model.HistoryEntry;
import service.task.manager.service.HistoryService;

import java.util.List;

/**
 * Controller for managing and retrieving the history of task accesses.
 */
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
@Tag(name = "History API", description = "API for retrieving the history of task accesses")
public class HistoryController {

    private final HistoryService service;

    /**
     * Retrieves the history of task accesses.
     * The history contains the last 10 records of calls to the findBy(long id) method for tasks, epics, and subtasks.
     *
     * @return a list of history entries
     */
    @GetMapping
    @Operation(summary = "Get task access history", description = "Retrieves the history of the last 10 task accesses (Task, Epic, Subtask) made via the findBy(long id) method.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = HistoryEntry.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error, possibly due to Redis connectivity issues",
                    content = @Content)
    })
    public List<HistoryEntry> getHistory() {
        try {
            log.info("Received request to retrieve task access history");
            List<HistoryEntry> history = service.getHistory();
            log.debug("Successfully retrieved history with {} entries", history.size());
            return history;
        } catch (Exception e) {
            log.error("Failed to retrieve task access history: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve history", e);
        }
    }
}