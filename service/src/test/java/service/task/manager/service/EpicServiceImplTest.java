package service.task.manager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.task.manager.dto.epic.EpicRequestCreatedDto;
import service.task.manager.dto.epic.EpicRequestUpdatedDto;
import service.task.manager.dto.epic.EpicResponseDto;
import service.task.manager.exception.ConflictException;
import service.task.manager.exception.NotFoundException;
import service.task.manager.mapper.EpicMapper;
import service.task.manager.model.Epic;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;
import service.task.manager.repository.EpicRepository;
import service.task.manager.service.impl.EpicServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EpicServiceImplTest {

    @Mock
    private EpicRepository repository;

    @Mock
    private EpicMapper mapper;

    @Mock
    private HistoryService history;

    @InjectMocks
    private EpicServiceImpl service;

    // Sample data for tests
    private final LocalDateTime now = LocalDateTime.now();
    private final Duration duration = Duration.ofHours(24);
    private final LocalDateTime endTime = now.plus(duration);

    // --- Tests for create method ---

    @Test
    void create_ShouldCreateEpic_WhenNameIsUnique() {
        // Arrange
        EpicRequestCreatedDto dto = new EpicRequestCreatedDto("New Epic", "Test epic created", now, duration);
        Epic epic = new Epic();
        epic.setName(dto.name());
        epic.setDescription(dto.description());
        epic.setStartTime(dto.startTime());
        epic.setDuration(dto.duration());
        epic.setStatus(Status.NEW);
        epic.setSubtasks(new ArrayList<>());

        when(repository.existsByName(dto.name())).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(epic);
        when(repository.save(any(Epic.class))).thenReturn(epic);

        // Act
        try {
            service.create(dto);
        } catch (Exception e) {
            System.out.println("Exception in create: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        // Assert
        verify(repository, times(1)).existsByName(dto.name());
        verify(mapper, times(1)).toEntity(dto);
        verify(repository, times(1)).save(epic);
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(endTime, epic.getEndTime());
        assertEquals(TaskType.EPIC, epic.getType());
    }

    @Test
    void create_ShouldThrowConflictException_WhenNameExists() {
        // Arrange
        EpicRequestCreatedDto dto = new EpicRequestCreatedDto("Existing Epic", "Description", now, duration);
        when(repository.existsByName(dto.name())).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> service.create(dto));
        verify(repository, times(1)).existsByName(dto.name());
        verify(mapper, never()).toEntity((EpicRequestCreatedDto) any());
        verify(repository, never()).save(any());
    }

    // --- Tests for update method ---

    @Test
    void update_ShouldUpdateEpic_WhenEpicExists() {
        // Arrange
        Long epicId = 1L;
        EpicRequestUpdatedDto dto = new EpicRequestUpdatedDto(epicId, "Updated Epic", "Updated Description", Status.IN_PROGRESS, duration);
        Epic existingEpic = new Epic();
        existingEpic.setId(epicId);
        existingEpic.setName("Old Epic");
        existingEpic.setDescription("Old Description");
        existingEpic.setStartTime(now.minusDays(1));
        existingEpic.setDuration(Duration.ofHours(12));
        existingEpic.setStatus(Status.NEW);
        existingEpic.setSubtasks(new ArrayList<>());

        EpicResponseDto responseDto = new EpicResponseDto(epicId, new ArrayList<>(), "Updated Epic", "Updated Description", Status.IN_PROGRESS, now.minusDays(1), duration, now.minusDays(1).plus(duration), TaskType.EPIC);

        when(repository.findById(epicId)).thenReturn(Optional.of(existingEpic));
        when(mapper.toResponseDto(existingEpic)).thenReturn(responseDto);
        when(mapper.toEntity(responseDto)).thenReturn(existingEpic);
        when(repository.save(existingEpic)).thenReturn(existingEpic);

        // Act
        EpicResponseDto result = service.update(dto);

        // Assert
        verify(repository, times(1)).findById(epicId);
        verify(mapper, times(1)).updateTaskFromDto(dto, existingEpic);
        verify(repository, times(1)).save(existingEpic);
        assertEquals("Updated Epic", result.name());
        assertEquals(now.minusDays(1).plus(duration), result.endTime());
        assertEquals(TaskType.EPIC, result.type());
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenEpicDoesNotExist() {
        // Arrange
        Long epicId = 1L;
        EpicRequestUpdatedDto dto = new EpicRequestUpdatedDto(epicId, "Updated Epic", "Updated Description", Status.IN_PROGRESS, duration);
        when(repository.findById(epicId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.update(dto));
        verify(repository, times(1)).findById(epicId);
        verify(mapper, never()).updateTaskFromDto(any(), any());
        verify(repository, never()).save(any());
    }

    // --- Tests for findById method ---

    @Test
    void findById_ShouldReturnEpic_WhenEpicExists() {
        // Arrange
        Long epicId = 1L;
        Epic epic = new Epic();
        epic.setId(epicId);
        epic.setName("Epic");
        epic.setDescription("Description");
        epic.setStartTime(now);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
        epic.setStatus(Status.NEW);
        epic.setSubtasks(new ArrayList<>());

        EpicResponseDto dto = new EpicResponseDto(epicId, new ArrayList<>(), "Epic", "Description", Status.NEW, now, duration, endTime, TaskType.EPIC);
        when(repository.findById(epicId)).thenReturn(Optional.of(epic));
        when(mapper.toResponseDto(epic)).thenReturn(dto);

        // Act
        EpicResponseDto result = service.findById(epicId);

        // Assert
        verify(repository, times(1)).findById(epicId);
        verify(mapper, times(1)).toResponseDto(epic);
        verify(history, times(1)).addToHistory(TaskType.EPIC, epicId);
        assertEquals(dto, result);
        assertEquals(endTime, result.endTime());
        assertEquals(TaskType.EPIC, result.type());
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenEpicDoesNotExist() {
        // Arrange
        Long epicId = 1L;
        when(repository.findById(epicId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.findById(epicId));
        verify(repository, times(1)).findById(epicId);
        verify(mapper, never()).toResponseDto(any());
        verify(history, never()).addToHistory(any(), any());
    }

    // --- Tests for findAll method ---

    @Test
    void findAll_ShouldReturnAllEpics() {
        // Arrange
        Epic epic1 = new Epic();
        epic1.setId(1L);
        epic1.setName("Epic1");
        epic1.setDescription("Desc1");
        epic1.setStartTime(now);
        epic1.setDuration(duration);
        epic1.setEndTime(endTime);
        epic1.setStatus(Status.NEW);
        epic1.setSubtasks(new ArrayList<>());

        Epic epic2 = new Epic();
        epic2.setId(2L);
        epic2.setName("Epic2");
        epic2.setDescription("Desc2");
        epic2.setStartTime(now);
        epic2.setDuration(duration);
        epic2.setEndTime(endTime);
        epic2.setStatus(Status.NEW);
        epic2.setSubtasks(new ArrayList<>());

        List<Epic> epics = List.of(epic1, epic2);
        EpicResponseDto dto1 = new EpicResponseDto(1L, new ArrayList<>(), "Epic1", "Desc1", Status.NEW, now, duration, endTime, TaskType.EPIC);
        EpicResponseDto dto2 = new EpicResponseDto(2L, new ArrayList<>(), "Epic2", "Desc2", Status.NEW, now, duration, endTime, TaskType.EPIC);
        when(repository.findAll()).thenReturn(epics);
        when(mapper.toResponseDto(epic1)).thenReturn(dto1);
        when(mapper.toResponseDto(epic2)).thenReturn(dto2);

        // Act
        List<EpicResponseDto> result = service.findAll();

        // Assert
        verify(repository, times(1)).findAll();
        verify(mapper, times(2)).toResponseDto(any(Epic.class));
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }
}