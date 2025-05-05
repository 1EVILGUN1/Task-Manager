package service.task.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import service.task.manager.model.HistoryEntry;
import service.task.manager.model.enums.TaskType;
import service.task.manager.service.impl.HistoryServiceImpl;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceImplTest {

    private static final String HISTORY_KEY = "history";
    private static final int HISTORY_SIZE = 10;

    @Mock
    private RedisTemplate<String, HistoryEntry> redisTemplate;

    @Mock
    private ListOperations<String, HistoryEntry> listOps;

    @InjectMocks
    private HistoryServiceImpl historyService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Use reflection to inject listOps into historyService
        Field listOpsField = HistoryServiceImpl.class.getDeclaredField("listOps");
        listOpsField.setAccessible(true);
        listOpsField.set(historyService, listOps);
    }

    @Test
    void addToHistory_ShouldAddEntrySuccessfully() {
        // Arrange
        TaskType taskType = TaskType.TASK;
        Long id = 1L;
        when(listOps.rightPush(eq(HISTORY_KEY), any(HistoryEntry.class))).thenReturn(1L);
        when(listOps.size(HISTORY_KEY)).thenReturn(1L);

        // Act
        historyService.addToHistory(taskType, id);

        // Assert
        verify(listOps, times(1)).rightPush(eq(HISTORY_KEY), any(HistoryEntry.class));
        verify(listOps, never()).leftPop(HISTORY_KEY);
    }

    @Test
    void addToHistory_ShouldRemoveOldestEntryWhenSizeExceedsLimit() {
        // Arrange
        TaskType taskType = TaskType.EPIC;
        Long id = 2L;
        when(listOps.rightPush(eq(HISTORY_KEY), any(HistoryEntry.class))).thenReturn(11L);
        when(listOps.size(HISTORY_KEY)).thenReturn(11L);
        when(listOps.leftPop(HISTORY_KEY)).thenReturn(new HistoryEntry(TaskType.TASK, 1L));

        // Act
        historyService.addToHistory(taskType, id);

        // Assert
        verify(listOps, times(1)).rightPush(eq(HISTORY_KEY), any(HistoryEntry.class));
        verify(listOps, times(1)).leftPop(HISTORY_KEY);
    }

    @Test
    void addToHistory_ShouldHandleExceptionGracefully() {
        // Arrange
        TaskType taskType = TaskType.SUBTASK;
        Long id = 3L;
        when(listOps.rightPush(eq(HISTORY_KEY), any(HistoryEntry.class)))
                .thenThrow(new RuntimeException("Redis exception"));

        // Act
        historyService.addToHistory(taskType, id);

        // Assert
        verify(listOps, times(1)).rightPush(eq(HISTORY_KEY), any(HistoryEntry.class));
        verify(listOps, never()).size(HISTORY_KEY);
        verify(listOps, never()).leftPop(HISTORY_KEY);
    }

    @Test
    void getHistory_ShouldReturnHistoryEntries() {
        // Arrange
        List<HistoryEntry> expectedHistory = List.of(
                new HistoryEntry(TaskType.TASK, 1L),
                new HistoryEntry(TaskType.EPIC, 2L)
        );
        when(listOps.range(HISTORY_KEY, 0, -1)).thenReturn(expectedHistory);

        // Act
        List<HistoryEntry> actualHistory = historyService.getHistory();

        // Assert
        assertEquals(expectedHistory, actualHistory);
        verify(listOps, times(1)).range(HISTORY_KEY, 0, -1);
    }

    @Test
    void getHistory_ShouldReturnEmptyListWhenHistoryIsNull() {
        // Arrange
        when(listOps.range(HISTORY_KEY, 0, -1)).thenReturn(null);

        // Act
        List<HistoryEntry> actualHistory = historyService.getHistory();

        // Assert
        assertNotNull(actualHistory);
        assertTrue(actualHistory.isEmpty());
        verify(listOps, times(1)).range(HISTORY_KEY, 0, -1);
    }

    @Test
    void getHistory_ShouldReturnEmptyListOnException() {
        // Arrange
        when(listOps.range(HISTORY_KEY, 0, -1)).thenThrow(new RuntimeException("Redis exception"));

        // Act
        List<HistoryEntry> actualHistory = historyService.getHistory();

        // Assert
        assertNotNull(actualHistory);
        assertTrue(actualHistory.isEmpty());
        verify(listOps, times(1)).range(HISTORY_KEY, 0, -1);
    }
}