package service.task.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import service.task.manager.dto.epic.EpicRequestCreatedDto;
import service.task.manager.dto.epic.EpicRequestUpdatedDto;
import service.task.manager.dto.epic.EpicResponseDto;
import service.task.manager.error.ConflictException;
import service.task.manager.error.NotFoundException;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;
import service.task.manager.service.EpicService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EpicController.class)
class EpicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EpicService epicService;

    @Autowired
    private ObjectMapper objectMapper;

    private EpicResponseDto epicResponseDto;

    @BeforeEach
    void setUp() {
        epicResponseDto = new EpicResponseDto(
                1L,
                Collections.emptyList(), // subtasks
                "Test Epic",
                "Test Description",
                Status.NEW,
                LocalDateTime.of(2025, 4, 27, 10, 0),
                Duration.ofHours(24),
                LocalDateTime.of(2025, 4, 28, 10, 0),
                TaskType.EPIC
        );
    }

    @Test
    void create_ValidEpic_ReturnsCreated() throws Exception {
        EpicRequestCreatedDto requestDto = new EpicRequestCreatedDto(
                "Test Epic",
                "Test Description",
                LocalDateTime.of(2025, 4, 27, 10, 0),
                Duration.ofHours(24)
        );

        doNothing().when(epicService).create(any(EpicRequestCreatedDto.class));

        mockMvc.perform(post("/epic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        verify(epicService, times(1)).create(any(EpicRequestCreatedDto.class));
    }

    @Test
    void create_DuplicateEpic_ReturnsConflict() throws Exception {
        EpicRequestCreatedDto requestDto = new EpicRequestCreatedDto(
                "Duplicate Epic",
                "Test Description",
                LocalDateTime.of(2025, 4, 27, 10, 0),
                Duration.ofHours(24)
        );

        doThrow(new ConflictException("Epic with the same name already exists"))
                .when(epicService).create(any(EpicRequestCreatedDto.class));

        mockMvc.perform(post("/epic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Epic with the same name already exists"));

        verify(epicService, times(1)).create(any(EpicRequestCreatedDto.class));
    }

    @Test
    void create_InvalidEpic_ReturnsBadRequest() throws Exception {
        EpicRequestCreatedDto requestDto = new EpicRequestCreatedDto(
                "", // Пустое имя, что нарушает валидацию
                "Test Description",
                LocalDateTime.of(2025, 4, 27, 10, 0),
                Duration.ofHours(24)
        );

        mockMvc.perform(post("/epic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("blank name"));

        verify(epicService, never()).create(any(EpicRequestCreatedDto.class));
    }

    @Test
    void update_ValidEpic_ReturnsOk() throws Exception {
        EpicRequestUpdatedDto requestDto = new EpicRequestUpdatedDto(
                1L,
                "Updated Epic",
                "Updated Description",
                Status.IN_PROGRESS,
                Duration.ofHours(48)
        );

        when(epicService.update(any(EpicRequestUpdatedDto.class))).thenReturn(epicResponseDto);

        mockMvc.perform(put("/epic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Epic"))
                .andExpect(jsonPath("$.status").value("NEW"));

        verify(epicService, times(1)).update(any(EpicRequestUpdatedDto.class));
    }

    @Test
    void update_EpicNotFound_ReturnsNotFound() throws Exception {
        EpicRequestUpdatedDto requestDto = new EpicRequestUpdatedDto(
                999L,
                "Updated Epic",
                "Updated Description",
                Status.IN_PROGRESS,
                Duration.ofHours(48)
        );

        when(epicService.update(any(EpicRequestUpdatedDto.class)))
                .thenThrow(new NotFoundException("Epic not found"));

        mockMvc.perform(put("/epic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Epic not found"));

        verify(epicService, times(1)).update(any(EpicRequestUpdatedDto.class));
    }

    @Test
    void findById_ValidId_ReturnsOk() throws Exception {
        when(epicService.findById(1L)).thenReturn(epicResponseDto);

        mockMvc.perform(get("/epic/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Epic"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.type").value("EPIC"));

        verify(epicService, times(1)).findById(1L);
    }

    @Test
    void findById_EpicNotFound_ReturnsNotFound() throws Exception {
        when(epicService.findById(999L)).thenThrow(new NotFoundException("Epic not found"));

        mockMvc.perform(get("/epic/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Epic not found"));

        verify(epicService, times(1)).findById(999L);
    }

    @Test
    void findById_InvalidId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/epic/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("id must be positive"));

        verify(epicService, never()).findById(anyLong());
    }

    @Test
    void findAll_ReturnsOk() throws Exception {
        List<EpicResponseDto> epics = List.of(epicResponseDto);
        when(epicService.findAll()).thenReturn(epics);

        mockMvc.perform(get("/epic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Epic"))
                .andExpect(jsonPath("$[0].status").value("NEW"))
                .andExpect(jsonPath("$[0].type").value("EPIC"));

        verify(epicService, times(1)).findAll();
    }

    @Test
    void findAll_EmptyList_ReturnsOk() throws Exception {
        when(epicService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/epic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(epicService, times(1)).findAll();
    }

    @Test
    void delete_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(epicService).delete(1L);

        mockMvc.perform(delete("/epic/1"))
                .andExpect(status().isNoContent());

        verify(epicService, times(1)).delete(1L);
    }

    @Test
    void delete_EpicNotFound_ReturnsNotFound() throws Exception {
        doThrow(new NotFoundException("Epic not found")).when(epicService).delete(999L);

        mockMvc.perform(delete("/epic/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Epic not found"));

        verify(epicService, times(1)).delete(999L);
    }

    @Test
    void delete_InvalidId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/epic/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("id must be positive"));

        verify(epicService, never()).delete(anyLong());
    }
}