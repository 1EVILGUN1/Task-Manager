package service.task.manager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.task.manager.dto.epic.EpicResponseDto;
import service.task.manager.dto.subtask.SubtaskRequestCreatedDto;
import service.task.manager.dto.subtask.SubtaskRequestUpdatedDto;
import service.task.manager.dto.subtask.SubtaskResponseDto;
import service.task.manager.exception.ConflictException;
import service.task.manager.exception.NotFoundException;
import service.task.manager.mapper.EpicMapper;
import service.task.manager.mapper.SubtaskMapper;
import service.task.manager.model.Epic;
import service.task.manager.model.Subtask;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;
import service.task.manager.repository.SubtaskRepository;
import service.task.manager.service.impl.SubtaskServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SubtaskServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class SubtaskServiceImplTest {

    @Mock
    private SubtaskRepository repository;

    @Mock
    private EpicService epicService;

    @Mock
    private SubtaskMapper mapper;

    @Mock
    private EpicMapper epicMapper;

    @Mock
    private HistoryService history;

    @InjectMocks
    private SubtaskServiceImpl service;

    // Sample data for tests
    private final LocalDateTime now = LocalDateTime.now();
    private final Duration duration = Duration.ofHours(24);
    private final LocalDateTime endTime = now.plus(duration);
    private final Long epicId = 1L;
    private final Long subtaskId = 1L;

    // --- Tests for create method ---

    @Test
    void create_ShouldCreateSubtask_WhenNameIsUniqueAndEpicExists() {
        // Arrange
        SubtaskRequestCreatedDto dto = new SubtaskRequestCreatedDto(epicId, "New Subtask", "Description", now, duration);
        EpicResponseDto epicDto = new EpicResponseDto(epicId, new ArrayList<>(), "Epic", "Epic Description", Status.NEW, now, duration, endTime, TaskType.EPIC);
        Epic epic = new Epic();
        epic.setId(epicId);
        epic.setName("Epic");
        epic.setDescription("Epic Description");
        epic.setSubtasks(new ArrayList<>());

        Subtask subtask = new Subtask();
        subtask.setName(dto.name());
        subtask.setDescription(dto.description());
        subtask.setStartTime(dto.startTime());
        subtask.setDuration(dto.duration());
        subtask.setEpic(epic);

        when(epicService.findById(epicId)).thenReturn(epicDto);
        when(epicMapper.toEntity(epicDto)).thenReturn(epic);
        when(repository.existsByName(dto.name())).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(subtask);
        when(repository.save(any(Subtask.class))).thenReturn(subtask);

        // Act
        service.create(dto);

        // Assert
        verify(epicService, times(1)).findById(epicId);
        verify(epicMapper, times(1)).toEntity(epicDto);
        verify(repository, times(1)).existsByName(dto.name());
        verify(mapper, times(1)).toEntity(dto);
        verify(repository, times(1)).save(subtask);
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(endTime, subtask.getEndTime());
        assertEquals(TaskType.SUBTASK, subtask.getType());
        assertEquals(epic, subtask.getEpic());
    }

    @Test
    void create_ShouldThrowConflictException_WhenNameExists() {
        // Arrange
        SubtaskRequestCreatedDto dto = new SubtaskRequestCreatedDto(epicId, "Existing Subtask", "Description", now, duration);
        EpicResponseDto epicDto = new EpicResponseDto(epicId, new ArrayList<>(), "Epic", "Epic Description", Status.NEW, now, duration, endTime, TaskType.EPIC);

        when(epicService.findById(epicId)).thenReturn(epicDto);
        when(repository.existsByName(dto.name())).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> service.create(dto));
        verify(epicService, times(1)).findById(epicId);
        verify(repository, times(1)).existsByName(dto.name());
        verify(mapper, never()).toEntity((SubtaskRequestCreatedDto) any());
        verify(repository, never()).save(any());
    }

    @Test
    void create_ShouldThrowNotFoundException_WhenEpicDoesNotExist() {
        // Arrange
        SubtaskRequestCreatedDto dto = new SubtaskRequestCreatedDto(epicId, "New Subtask", "Description", now, duration);
        when(epicService.findById(epicId)).thenThrow(new NotFoundException("Epic with ID " + epicId + " not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.create(dto));
        verify(epicService, times(1)).findById(epicId);
        verify(repository, never()).existsByName(any());
        verify(mapper, never()).toEntity((SubtaskRequestCreatedDto) any());
        verify(repository, never()).save(any());
    }

    // --- Tests for update method ---

    @Test
    void update_ShouldUpdateSubtask_WhenSubtaskExists() {
        // Arrange
        SubtaskRequestUpdatedDto dto = new SubtaskRequestUpdatedDto(subtaskId, "Updated Subtask", "Updated Description", Status.IN_PROGRESS, duration);
        Subtask existingSubtask = new Subtask();
        existingSubtask.setId(subtaskId);
        existingSubtask.setName("Old Subtask");
        existingSubtask.setDescription("Old Description");
        existingSubtask.setStartTime(now.minusDays(1));
        existingSubtask.setDuration(Duration.ofHours(12));
        existingSubtask.setStatus(Status.NEW);

        SubtaskResponseDto responseDto = new SubtaskResponseDto(subtaskId, epicId, "Updated Subtask", "Updated Description", Status.IN_PROGRESS, now.minusDays(1), now.minusDays(1).plus(duration), duration, TaskType.SUBTASK);

        when(repository.findById(subtaskId)).thenReturn(Optional.of(existingSubtask));
        when(mapper.toResponseDto(existingSubtask)).thenReturn(responseDto);
        when(mapper.toEntity(responseDto)).thenReturn(existingSubtask);
        when(repository.save(existingSubtask)).thenReturn(existingSubtask);

        // Act
        SubtaskResponseDto result = service.update(dto);

        // Assert
        verify(repository, times(1)).findById(subtaskId);
        verify(mapper, times(1)).updateSubtaskFromDto(dto, existingSubtask);
        verify(repository, times(1)).save(existingSubtask);
        assertEquals("Updated Subtask", result.name());
        assertEquals(now.minusDays(1).plus(duration), result.endTime());
        assertEquals(TaskType.SUBTASK, result.type());
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenSubtaskDoesNotExist() {
        // Arrange
        SubtaskRequestUpdatedDto dto = new SubtaskRequestUpdatedDto(subtaskId, "Updated Subtask", "Updated Description", Status.IN_PROGRESS, duration);
        when(repository.findById(subtaskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.update(dto));
        verify(repository, times(1)).findById(subtaskId);
        verify(mapper, never()).updateSubtaskFromDto(any(), any());
        verify(repository, never()).save(any());
    }

    // --- Tests for findById method ---

    @Test
    void findById_ShouldReturnSubtask_WhenSubtaskExists() {
        // Arrange
        Subtask subtask = new Subtask();
        subtask.setId(subtaskId);
        subtask.setName("Subtask");
        subtask.setDescription("Description");
        subtask.setStartTime(now);
        subtask.setDuration(duration);
        subtask.setEndTime(endTime);
        subtask.setStatus(Status.NEW);
        Epic epic = new Epic();
        epic.setId(epicId);
        subtask.setEpic(epic);

        SubtaskResponseDto dto = new SubtaskResponseDto(subtaskId, epicId, "Subtask", "Description", Status.NEW, now, endTime, duration, TaskType.SUBTASK);
        when(repository.findById(subtaskId)).thenReturn(Optional.of(subtask));
        when(mapper.toResponseDto(subtask)).thenReturn(dto);

        // Act
        SubtaskResponseDto result = service.findById(subtaskId);

        // Assert
        verify(repository, times(1)).findById(subtaskId);
        verify(mapper, times(1)).toResponseDto(subtask);
        verify(history, times(1)).addToHistory(TaskType.SUBTASK, subtaskId);
        assertEquals(dto, result);
        assertEquals(endTime, result.endTime());
        assertEquals(TaskType.SUBTASK, result.type());
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenSubtaskDoesNotExist() {
        // Arrange
        when(repository.findById(subtaskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.findById(subtaskId));
        verify(repository, times(1)).findById(subtaskId);
        verify(mapper, never()).toResponseDto(any());
        verify(history, never()).addToHistory(any(), any());
    }

    // --- Tests for findAll method ---

    @Test
    void findAll_ShouldReturnAllSubtasks() {
        // Arrange
        Subtask subtask1 = new Subtask();
        subtask1.setId(1L);
        subtask1.setName("Subtask1");
        subtask1.setDescription("Desc1");
        subtask1.setStartTime(now);
        subtask1.setDuration(duration);
        subtask1.setEndTime(endTime);
        subtask1.setStatus(Status.NEW);
        Epic epic1 = new Epic();
        epic1.setId(epicId);
        subtask1.setEpic(epic1);

        Subtask subtask2 = new Subtask();
        subtask2.setId(2L);
        subtask2.setName("Subtask2");
        subtask2.setDescription("Desc2");
        subtask2.setStartTime(now);
        subtask2.setDuration(duration);
        subtask2.setEndTime(endTime);
        subtask2.setStatus(Status.NEW);
        subtask2.setEpic(epic1);

        List<Subtask> subtasks = List.of(subtask1, subtask2);
        SubtaskResponseDto dto1 = new SubtaskResponseDto(1L, epicId, "Subtask1", "Desc1", Status.NEW, now, endTime, duration, TaskType.SUBTASK);
        SubtaskResponseDto dto2 = new SubtaskResponseDto(2L, epicId, "Subtask2", "Desc2", Status.NEW, now, endTime, duration, TaskType.SUBTASK);
        when(repository.findAll()).thenReturn(subtasks);
        when(mapper.toResponseDto(subtask1)).thenReturn(dto1);
        when(mapper.toResponseDto(subtask2)).thenReturn(dto2);

        // Act
        List<SubtaskResponseDto> result = service.findAll();

        // Assert
        verify(repository, times(1)).findAll();
        verify(mapper, times(2)).toResponseDto(any(Subtask.class));
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    // --- Tests for delete method ---

    @Test
    void delete_ShouldDeleteSubtask_WhenSubtaskExists() {
        // Arrange
        Subtask subtask = new Subtask();
        subtask.setId(subtaskId);
        subtask.setName("Subtask");
        subtask.setDescription("Description");
        subtask.setStartTime(now);
        subtask.setDuration(duration);
        subtask.setEndTime(endTime);
        subtask.setStatus(Status.NEW);
        Epic epic = new Epic();
        epic.setId(epicId);
        subtask.setEpic(epic);

        SubtaskResponseDto dto = new SubtaskResponseDto(subtaskId, epicId, "Subtask", "Description", Status.NEW, now, endTime, duration, TaskType.SUBTASK);
        when(repository.findById(subtaskId)).thenReturn(Optional.of(subtask));
        when(mapper.toResponseDto(subtask)).thenReturn(dto);

        // Act
        service.delete(subtaskId);

        // Assert
        verify(repository, times(1)).findById(subtaskId);
        verify(repository, times(1)).deleteById(subtaskId);
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenSubtaskDoesNotExist() {
        // Arrange
        when(repository.findById(subtaskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.delete(subtaskId));
        verify(repository, times(1)).findById(subtaskId);
        verify(repository, never()).deleteById(any());
    }
}
