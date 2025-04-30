package service.task.manager.service;

import service.task.manager.model.HistoryEntry;
import service.task.manager.model.enums.TaskType;

import java.util.List;

public interface HistoryService {

    void addToHistory(TaskType type, Long id);

    List<HistoryEntry> getHistory();
}
