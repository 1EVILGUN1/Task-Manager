package service.task.manager.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import service.task.manager.model.HistoryEntry;
import service.task.manager.model.enums.TaskType;
import service.task.manager.service.HistoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the service for managing the history of accesses to tasks (Task, Epic, Subtask) using Redis.
 * The history stores the last 10 records of calls to the findBy(long id) method for tasks, epics, and subtasks.
 * If the number of records exceeds the limit, the oldest record is removed.
 */
@Slf4j
@Service
public class HistoryServiceImpl implements HistoryService {

    private static final String HISTORY_KEY = "history";
    private static final int HISTORY_SIZE = 10;

    private final RedisTemplate<String, HistoryEntry> redisTemplate;
    private final ListOperations<String, HistoryEntry> listOps;

    @Autowired
    public HistoryServiceImpl(RedisTemplate<String, HistoryEntry> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listOps = redisTemplate.opsForList();
    }

    /**
     * Adds a record to the history of findBy(long id) method calls.
     * If the history size exceeds the limit (10 records), the oldest record is removed.
     *
     * @param type the type of the task (TASK, EPIC, SUBTASK)
     * @param id   the identifier of the task
     */
    @Override
    public void addToHistory(TaskType type, Long id) {
        try {
            HistoryEntry entry = new HistoryEntry(type, id);
            log.info("Adding entry to history: type={}, id={}", type, id);
            listOps.rightPush(HISTORY_KEY, entry);
            Long size = listOps.size(HISTORY_KEY);
            if (size != null && size > HISTORY_SIZE) {
                log.debug("History size exceeded limit ({}), removing oldest entry", HISTORY_SIZE);
                listOps.leftPop(HISTORY_KEY);
            }
        } catch (Exception e) {
            log.error("Failed to add entry to history: type={}, id={}, exception={}", type, id, e.getMessage(), e);
        }
    }

    /**
     * Retrieves the list of entries from the history of method calls.
     * If the history is empty or an exception occurs while retrieving data, an empty list is returned.
     *
     * @return the list of history entries
     */
    @Override
    public List<HistoryEntry> getHistory() {
        try {
            log.info("Retrieving call history");
            List<HistoryEntry> history = listOps.range(HISTORY_KEY, 0, -1);
            if (history == null) {
                log.warn("History is empty or failed to retrieve data from Redis");
                return new ArrayList<>();
            }
            log.debug("Successfully retrieved {} entries from history", history.size());
            return history;
        } catch (Exception e) {
            log.error("Failed to retrieve history: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}