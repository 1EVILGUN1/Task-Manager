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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceImplTest {

    @Mock
    private RedisTemplate<String, HistoryEntry> redisTemplate;

    @Mock
    private ListOperations<String, HistoryEntry> listOps;

    @InjectMocks
    private HistoryServiceImpl historyService;

    @BeforeEach
    void setUp() {
        // Ensure redisTemplate.opsForList() returns the mocked listOps
        when(redisTemplate.opsForList()).thenReturn(listOps);
    }

    @Test
    void addToHistory_WithinLimit_AddsEntry() {
        // Mock the size of the list and the range result
        when(listOps.size("history")).thenReturn(1L);
        when(listOps.range("history", 0, -1)).thenReturn(List.of(new HistoryEntry(TaskType.EPIC, 1L)));

        // Call the method under test
        historyService.addToHistory(TaskType.EPIC, 1L);
        List<HistoryEntry> history = historyService.getHistory();

        // Verify the result
        assertEquals(1, history.size());
        assertEquals(TaskType.EPIC, history.get(0).getType());
        assertEquals(1L, history.get(0).getId());

        // Verify interactions with listOps
        verify(listOps, times(1)).rightPush("history", new HistoryEntry(TaskType.EPIC, 1L));
    }

    @Test
    void addToHistory_ExceedsLimit_RemovesOldest() {
        // Create a list to simulate the history entries
        List<HistoryEntry> historyEntries = new ArrayList<>();

        // Simulate adding 11 entries
        for (long i = 1; i <= 11; i++) {
            // Mock the size of the list to increase with each addition
            when(listOps.size("history")).thenReturn(i);
            historyService.addToHistory(TaskType.TASK, i);
            historyEntries.add(new HistoryEntry(TaskType.TASK, i));

            // If size exceeds MAX_HISTORY_SIZE (10), simulate leftPop
            if (i > 10) {
                historyEntries.remove(0); // Remove the oldest entry
            }
        }

        // Mock the final state of the history after trimming
        when(listOps.range("history", 0, -1)).thenReturn(historyEntries);

        // Call getHistory to retrieve the result
        List<HistoryEntry> history = historyService.getHistory();

        // Verify the result
        assertEquals(10, history.size());
        assertEquals(2L, history.get(0).getId()); // First entry (id=1) should be removed
        assertEquals(11L, history.get(9).getId());

        // Verify interactions with listOps
        verify(listOps, times(11)).rightPush(eq("history"), any(HistoryEntry.class));
        verify(listOps, times(1)).leftPop("history"); // Should trim once after exceeding limit
    }
}