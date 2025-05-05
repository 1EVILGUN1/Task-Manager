package service.task.manager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.task.manager.dto.task.TaskRequestCreatedDto;
import service.task.manager.dto.task.TaskRequestUpdatedDto;
import service.task.manager.dto.task.TaskResponseDto;
import service.task.manager.exception.ConflictException;
import service.task.manager.exception.NotFoundException;
import service.task.manager.mapper.TaskMapper;
import service.task.manager.model.Task;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;
import service.task.manager.repository.TaskRepository;
import service.task.manager.service.impl.TaskServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskServiceImpl, aligned with updated DTOs and Task model.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private TaskMapper mapper;

    @Mock
    private HistoryService history;

    @InjectMocks
    private TaskServiceImpl taskService;

    // Sample data for tests
    private final LocalDateTime now = LocalDateTime.now();
    private final Duration duration = Duration.ofHours(24);
    private final LocalDateTime endTime = now.plus(duration);

    // --- Tests for create method ---

    @Test
    void create_ShouldCreateTask_WhenNameIsUnique() {
        // Arrange
        TaskRequestCreatedDto dto = new TaskRequestCreatedDto("New Task", "Description", now, duration);
        Task task = new Task();
        task.setName(dto.name());
        task.setDescription(dto.description());
        task.setStartTime(dto.startTime());
        task.setDuration(dto.duration());

        when(repository.existsByName(dto.name())).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(task);
        when(repository.save(any(Task.class))).thenReturn(task);

        // Act
        taskService.create(dto);

        // Assert
        verify(repository, times(1)).existsByName(dto.name());
        verify(mapper, times(1)).toEntity(dto);
        verify(repository, times(1)).save(task);
        assertEquals(Status.NEW, task.getStatus()); // Status set by addEndTimeTaskAndStatus
        assertEquals(endTime, task.getEndTime()); // End time calculated
        assertEquals(TaskType.TASK, task.getType()); // Default type
    }

    @Test
    void create_ShouldThrowConflictException_WhenNameExists() {
        // Arrange
        TaskRequestCreatedDto dto = new TaskRequestCreatedDto("Existing Task", "Description", now, duration);
        when(repository.existsByName(dto.name())).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> taskService.create(dto));
        verify(repository, times(1)).existsByName(dto.name());
        verify(mapper, never()).toEntity((TaskRequestCreatedDto) any());
        verify(repository, never()).save(any());
    }

    // --- Tests for update method ---

    @Test
    void update_ShouldUpdateTask_WhenTaskExists() {
        // Arrange
        Long taskId = 1L;
        TaskRequestUpdatedDto dto = new TaskRequestUpdatedDto(taskId, "Updated Task", "Updated Description", Status.IN_PROGRESS, duration);
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setName("Old Task");
        existingTask.setDescription("Old Description");
        existingTask.setStartTime(now.minusDays(1));
        existingTask.setDuration(Duration.ofHours(12));
        existingTask.setStatus(Status.NEW);

        TaskResponseDto responseDto = new TaskResponseDto(taskId, "Updated Task", "Updated Description", Status.IN_PROGRESS, now.minusDays(1), now.minusDays(1).plus(duration), duration, TaskType.TASK);

        when(repository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(mapper.toResponseDto(existingTask)).thenReturn(responseDto);
        when(mapper.toEntity(responseDto)).thenReturn(existingTask);
        when(repository.save(existingTask)).thenReturn(existingTask);

        // Act
        TaskResponseDto result = taskService.update(dto);

        // Assert
        verify(repository, times(1)).findById(taskId); // Called by findById internally
        verify(mapper, times(1)).updateTaskFromDto(dto, existingTask);
        verify(repository, times(1)).save(existingTask);
        assertEquals("Updated Task", result.name());
        assertEquals(now.minusDays(1).plus(duration), result.endTime()); // End time recalculated
        assertEquals(TaskType.TASK, result.type());
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenTaskDoesNotExist() {
        // Arrange
        Long taskId = 1L;
        TaskRequestUpdatedDto dto = new TaskRequestUpdatedDto(taskId, "Updated Task", "Updated Description", Status.IN_PROGRESS, duration);
        when(repository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> taskService.update(dto));
        verify(repository, times(1)).findById(taskId);
        verify(mapper, never()).updateTaskFromDto(any(), any());
        verify(repository, never()).save(any());
    }

    // --- Tests for findById method ---

    @Test
    void findById_ShouldReturnTask_WhenTaskExists() {
        // Arrange
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setName("Task");
        task.setDescription("Description");
        task.setStartTime(now);
        task.setDuration(duration);
        task.setEndTime(endTime);
        task.setStatus(Status.NEW);

        TaskResponseDto dto = new TaskResponseDto(taskId, "Task", "Description", Status.NEW, now, endTime, duration, TaskType.TASK);
        when(repository.findById(taskId)).thenReturn(Optional.of(task));
        when(mapper.toResponseDto(task)).thenReturn(dto);

        // Act
        TaskResponseDto result = taskService.findById(taskId);

        // Assert
        verify(repository, times(1)).findById(taskId);
        verify(mapper, times(1)).toResponseDto(task);
        verify(history, times(1)).addToHistory(TaskType.TASK, taskId);
        assertEquals(dto, result);
        assertEquals(endTime, result.endTime());
        assertEquals(TaskType.TASK, result.type());
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenTaskDoesNotExist() {
        // Arrange
        Long taskId = 1L;
        when(repository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> taskService.findById(taskId));
        verify(repository, times(1)).findById(taskId);
        verify(mapper, never()).toResponseDto(any());
        verify(history, never()).addToHistory(any(), any());
    }

    // --- Tests for findAll method ---

    @Test
    void findAll_ShouldReturnAllTasks() {
        // Arrange
        Task task1 = new Task();
        task1.setId(1L);
        task1.setName("Task1");
        task1.setDescription("Desc1");
        task1.setStartTime(now);
        task1.setDuration(duration);
        task1.setEndTime(endTime);
        task1.setStatus(Status.NEW);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setName("Task2");
        task2.setDescription("Desc2");
        task2.setStartTime(now);
        task2.setDuration(duration);
        task2.setEndTime(endTime);
        task2.setStatus(Status.NEW);

        List<Task> tasks = List.of(task1, task2);
        TaskResponseDto dto1 = new TaskResponseDto(1L, "Task1", "Desc1", Status.NEW, now, endTime, duration, TaskType.TASK);
        TaskResponseDto dto2 = new TaskResponseDto(2L, "Task2", "Desc2", Status.NEW, now, endTime, duration, TaskType.TASK);
        when(repository.findAll()).thenReturn(tasks);
        when(mapper.toResponseDto(task1)).thenReturn(dto1);
        when(mapper.toResponseDto(task2)).thenReturn(dto2);

        // Act
        List<TaskResponseDto> result = taskService.findAll();

        // Assert
        verify(repository, times(1)).findAll();
        verify(mapper, times(2)).toResponseDto(any(Task.class));
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    // --- Tests for delete method ---

    @Test
    void delete_ShouldDeleteTask_WhenTaskExists() {
        // Arrange
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setName("Task");
        task.setDescription("Description");
        task.setStartTime(now);
        task.setDuration(duration);
        task.setEndTime(endTime);
        task.setStatus(Status.NEW);

        TaskResponseDto dto = new TaskResponseDto(taskId, "Task", "Description",
                Status.NEW, now, endTime, duration, TaskType.TASK);
        when(repository.findById(taskId)).thenReturn(Optional.of(task));
        when(mapper.toResponseDto(task)).thenReturn(dto);

        // Act
        taskService.delete(taskId);

        // Assert
        verify(repository, times(1)).findById(taskId); // Called by findById internally
        verify(repository, times(1)).deleteById(taskId);
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenTaskDoesNotExist() {
        // Arrange
        Long taskId = 1L;
        when(repository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> taskService.delete(taskId));
        verify(repository, times(1)).findById(taskId);
        verify(repository, never()).deleteById(any());
    }
}