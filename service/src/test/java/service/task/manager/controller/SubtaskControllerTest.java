package service.task.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import service.task.manager.dto.subtask.SubtaskRequestCreatedDto;
import service.task.manager.dto.subtask.SubtaskRequestUpdatedDto;
import service.task.manager.dto.subtask.SubtaskResponseDto;
import service.task.manager.exception.ConflictException;
import service.task.manager.exception.ErrorHandler;
import service.task.manager.exception.NotFoundException;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;
import service.task.manager.service.SubtaskService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {SubtaskController.class, ErrorHandler.class})
class SubtaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubtaskService subtaskService;

    private final LocalDateTime testTime = LocalDateTime.of(2025, 4, 27, 10, 0);
    private final Duration testDuration = Duration.ofHours(24);

    // CREATE tests
    @Test
    void createSubtask_ValidRequest_ReturnsCreated() throws Exception {
        SubtaskRequestCreatedDto request = new SubtaskRequestCreatedDto(
                1L,
                "Valid Subtask",
                "Valid Description",
                testTime,
                testDuration
        );

        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        verify(subtaskService).create(request);
    }

    @Test
    void createSubtask_ConflictName_ReturnsConflict() throws Exception {
        SubtaskRequestCreatedDto request = new SubtaskRequestCreatedDto(
                1L,
                "Duplicate Subtask",
                "Description",
                testTime,
                testDuration
        );

        doThrow(new ConflictException("Subtask with name Duplicate Subtask already exists"))
                .when(subtaskService).create(request); // Используем конкретный request

        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Subtask with name Duplicate Subtask already exists"));
    }

    // UPDATE tests
    @Test
    void updateSubtask_ValidRequest_ReturnsOk() throws Exception {
        SubtaskRequestUpdatedDto request = new SubtaskRequestUpdatedDto(
                1L,
                "Updated Subtask",
                "Updated Description",
                Status.IN_PROGRESS,
                Duration.ofHours(48)
        );

        SubtaskResponseDto responseDto = new SubtaskResponseDto(
                1L,
                1L,
                "Updated Subtask",
                "Updated Description",
                Status.IN_PROGRESS,
                testTime,
                testTime.plus(Duration.ofHours(48)),
                Duration.ofHours(48),
                TaskType.SUBTASK
        );

        when(subtaskService.update(any())).thenReturn(responseDto);

        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Subtask"))
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()));

        verify(subtaskService).update(request); // Проверяем вызов сервиса
    }

    @Test
    void updateSubtask_NotFound_ReturnsNotFound() throws Exception {
        SubtaskRequestUpdatedDto request = new SubtaskRequestUpdatedDto(
                999L,
                "Non-existent Subtask",
                "Description",
                Status.NEW,
                testDuration
        );

        when(subtaskService.update(any()))
                .thenThrow(new NotFoundException("Subtask with ID 999 not found"));

        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Subtask with ID 999 not found"));
    }

    // GET tests
    @Test
    void getSubtask_ValidId_ReturnsOk() throws Exception {
        SubtaskResponseDto responseDto = new SubtaskResponseDto(
                1L,
                1L,
                "Test Subtask",
                "Test Description",
                Status.NEW,
                testTime,
                testTime.plus(testDuration),
                testDuration,
                TaskType.SUBTASK
        );

        when(subtaskService.findById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/subtask/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.epicId").value(1L))
                .andExpect(jsonPath("$.name").value("Test Subtask"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.startTime").value("2025-04-27T10:00:00"))
                .andExpect(jsonPath("$.endTime").value("2025-04-28T10:00:00"))
                .andExpect(jsonPath("$.duration").value("PT24H"))
                .andExpect(jsonPath("$.type").value("SUBTASK"))
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()));
    }

    @Test
    void getSubtask_InvalidId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/subtask/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("id must be positive"))
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()));
    }

    @Test
    void getSubtask_NotFound_ReturnsNotFound() throws Exception {
        when(subtaskService.findById(999L))
                .thenThrow(new NotFoundException("Subtask with ID 999 not found"));

        mockMvc.perform(get("/subtask/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Subtask with ID 999 not found"));
    }

    // GET ALL tests
    @Test
    void getAllSubtasks_ReturnsOk() throws Exception {
        List<SubtaskResponseDto> subtasks = Collections.singletonList(
                new SubtaskResponseDto(
                        1L,
                        1L,
                        "Subtask",
                        "Description",
                        Status.NEW,
                        testTime,
                        testTime.plus(testDuration),
                        testDuration,
                        TaskType.SUBTASK
                )
        );

        when(subtaskService.findAll()).thenReturn(subtasks);

        mockMvc.perform(get("/subtask"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()));
    }

    // DELETE tests
    @Test
    void deleteSubtask_ValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/subtask/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string("")); // Проверяем, что тело ответа пустое

        verify(subtaskService).delete(1L);
    }

    @Test
    void deleteSubtask_NotFound_ReturnsNotFound() throws Exception {
        doThrow(new NotFoundException("Subtask with ID 999 not found"))
                .when(subtaskService).delete(999L);

        mockMvc.perform(delete("/subtask/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Subtask with ID 999 not found"));
    }

    // Validation tests
    @Test
    void createSubtask_InvalidRequest_ReturnsBadRequest() throws Exception {
        SubtaskRequestCreatedDto invalidRequest = new SubtaskRequestCreatedDto(
                null, // Missing epicId
                "",   // Blank name
                "",   // Blank description
                null, // Missing startTime
                null  // Missing duration
        );

        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.epicId").value("null epic ID"))
                .andExpect(jsonPath("$.name").value("blank name"))
                .andExpect(jsonPath("$.description").value("blank description"))
                .andExpect(jsonPath("$.startTime").value("null start time"))
                .andExpect(jsonPath("$.duration").value("null duration"));
    }
}