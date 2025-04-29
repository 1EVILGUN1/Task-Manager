package service.task.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import service.task.manager.dto.task.TaskRequestCreatedDto;
import service.task.manager.dto.task.TaskRequestUpdatedDto;
import service.task.manager.dto.task.TaskResponseDto;
import service.task.manager.error.ErrorHandler;
import service.task.manager.error.NotFoundException;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;
import service.task.manager.service.TaskService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TaskController.class, ErrorHandler.class})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private final LocalDateTime testTime = LocalDateTime.of(2025, 4, 27, 10, 0);
    private final Duration testDuration = Duration.ofHours(24);

    // CREATE tests
    @Test
    void createTask_ValidRequest_ReturnsCreated() throws Exception {
        TaskRequestCreatedDto request = new TaskRequestCreatedDto(
                "Valid Task",
                "Valid Description",
                testTime,
                testDuration
        );

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(taskService).create(request);
    }

    @Test
    void createTask_InvalidRequest_ReturnsBadRequest() throws Exception {
        TaskRequestCreatedDto invalidRequest = new TaskRequestCreatedDto(
                "", // Invalid blank name
                "", // Invalid blank description
                null, // Missing start time
                null  // Missing duration
        );

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void updateTask_ValidRequest_ReturnsOk() throws Exception {
        TaskRequestUpdatedDto request = new TaskRequestUpdatedDto(
                1L,
                "Updated Task",
                "Updated Description",
                Status.IN_PROGRESS,
                Duration.ofHours(48)
        );

        TaskResponseDto responseDto = new TaskResponseDto(
                1L,
                "Updated Task",
                "Updated Description",
                Status.IN_PROGRESS,
                testTime,
                testTime.plus(Duration.ofHours(48)),
                Duration.ofHours(48),
                TaskType.TASK
        );

        when(taskService.update(any(TaskRequestUpdatedDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Task"));
    }

    @Test
    void updateTask_InvalidId_ReturnsBadRequest() throws Exception {
        TaskRequestUpdatedDto invalidRequest = new TaskRequestUpdatedDto(
                -1L, // Invalid ID
                "Task",
                "Description",
                Status.NEW,
                testDuration
        );

        mockMvc.perform(put("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // GET tests
    @Test
    void getTask_ValidId_ReturnsOk() throws Exception {
        TaskResponseDto responseDto = new TaskResponseDto(
                1L,
                "Test Task",
                "Test Description",
                Status.NEW,
                testTime,
                testTime.plus(testDuration),
                testDuration,
                TaskType.TASK
        );

        when(taskService.findById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getTask_InvalidId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/task/-1"))
                .andExpect(status().isBadRequest());
    }

    // GET ALL tests
    @Test
    void getAllTasks_ReturnsOk() throws Exception {
        List<TaskResponseDto> tasks = Collections.singletonList(
                new TaskResponseDto(
                        1L,
                        "Task",
                        "Description",
                        Status.NEW,
                        testTime,
                        testTime.plus(testDuration),
                        testDuration,
                        TaskType.TASK
                )
        );

        when(taskService.findAll()).thenReturn(tasks);

        mockMvc.perform(get("/task"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    // DELETE tests
    @Test
    void deleteTask_ValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/task/1"))
                .andExpect(status().isNoContent());

        verify(taskService).delete(1L);
    }

    @Test
    void deleteTask_InvalidId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/task/-1"))
                .andExpect(status().isBadRequest());
    }

    // Exception handling tests
    @Test
    void updateTask_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        Long nonExistentTaskId = 999L;
        TaskRequestUpdatedDto request = new TaskRequestUpdatedDto(
                nonExistentTaskId,
                "Non-existent Task",
                "Description",
                Status.NEW,
                testDuration
        );

        String expectedErrorMessage = "Task with ID " + nonExistentTaskId + " not found";

        when(taskService.update(any(TaskRequestUpdatedDto.class)))
                .thenThrow(new NotFoundException(expectedErrorMessage));
        mockMvc.perform(put("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedErrorMessage));

        verify(taskService).update(any(TaskRequestUpdatedDto.class));
    }

    @Test
    void getTask_NotFound_ReturnsNotFound() throws Exception {
        when(taskService.findById(999L)).thenThrow(new NotFoundException("Task not found"));

        mockMvc.perform(get("/task/999"))
                .andExpect(status().isNotFound());
    }
}