package task.manager.schedule.service.inMemory;

import task.manager.schedule.exception.ManagerSaveException;
import task.manager.schedule.exception.NotFoundException;
import task.manager.schedule.model.Epic;
import task.manager.schedule.model.Status;
import task.manager.schedule.model.Subtask;
import task.manager.schedule.model.Task;
import task.manager.schedule.service.HistoryManager;
import task.manager.schedule.service.TaskManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    protected int counterId = 0;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;
    protected final Set<Task> prioritizedTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager; // 3
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public Task createTask(Task task) {// создание Task
        final int id = ++counterId;
        task.setId(id);
        addPriorityTask(task);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {// создание Epic
        final int id = ++counterId;
        epic.setId(id);
        addPriorityTask(epic);
        epics.put(id, epic);
        return epic;
    }


    @Override
    public Subtask createSubtask(Subtask subtask) {// создание Subtask
        final int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        updateEpicDuration(epic);
        final int id = ++counterId;
        subtask.setId(id);
        addPriorityTask(subtask);
        subtasks.put(id, subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epicId);
        return subtask;
    }

    @Override
    public void updateTask(Task task) {// обновление Task
        final Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        addPriorityTask(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {// обновление Epic
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        addPriorityTask(savedEpic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {// обновление Subtask
        final int epicId = subtask.getEpicId();
        final Subtask savedSubtask = subtasks.get(subtask.getId());
        if (savedSubtask == null) {
            return;
        }
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        addPriorityTask(savedSubtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpic(epicId);// обновление статуса у Epic
    }

    @Override
    public void cleanTasks() {
        tasks.clear();
    }// очистка списка Tasks

    public void cleanSubtasks() {// очистка списка Subtasks
        for (Epic epic : epics.values()) {
            epic.cleanSubtask();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    @Override
    public void cleanEpics() {// очистка списка Epics и Subtasks
        epics.clear();
        subtasks.clear();

    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }// удаление по id Task

    @Override
    public void deleteSubtask(int id) {// удаление по id Subtask
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void deleteEpic(int id) {// удаление по id Epic
        Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }// получаем список Tasks

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }// получаем список Epics

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {// получаем список Epic с Subtasks
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Задача с ид=" + epicId);
        }
        updateEpic(epicId);
        return epic.getSubtaskIds().stream().map(subtasks::get).collect(Collectors.toList());
    }

    @Override
    public Task getTask(int id) {// получаем Task по id
        final Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с ид=" + id);
        }
        historyManager.add(task);
        return task;

    }

    @Override
    public Epic getEpic(int id) {// получаем Epic по id
        final Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с ид=" + id);
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {// получаем Subtask по id
        final Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с ид=" + id);
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getHistory() {// получаем список истории
        return historyManager.getAll();
    }

    private void updateEpicStatus(int epicId) {// обновление статуса Epic
        Epic epic = epics.get(epicId);
        List<Subtask> subtasks = epic.getSubtaskIds().stream()
                .filter(this.subtasks::containsKey)
                .map(this.subtasks::get)
                .toList();
        for (Subtask statusSubtask : subtasks) {
            short subtaskNew = 0;
            short subtaskDone = 0;
            if (statusSubtask.getStatus() == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                break;
            } else if (statusSubtask.getStatus() == Status.NEW) {
                subtaskNew++;
            } else if (statusSubtask.getStatus() == Status.DONE) {
                subtaskDone++;
            }
            if (subtaskDone == subtasks.size()) {
                epic.setStatus(Status.DONE);
                break;
            }
            if (subtaskNew == subtasks.size()) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
            break;
        }
    }

    private void updateEpicDuration(Epic epic) {
        List<Integer> subs = epic.getSubtaskIds();
        if (subs.isEmpty()) {
            epic.setDuration(0L);
            return;
        }
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        long duration = 0L;
        for (int id : subs) {
            final Subtask subtask = subtasks.get(id);
            final LocalDateTime startTime = subtask.getStartTime();
            final LocalDateTime endTime = subtask.getEndTime();
            if (startTime.isBefore(start)) {
                start = startTime;
            }
            if (endTime.isAfter(end)) {
                end = endTime;
            }
            duration += subtask.getDuration();
        }
        epic.setDuration(duration);
        epic.setStartTime(start);
        epic.setEndTime(end);
    }

    protected void updateEpic(int epicId) {
        Epic epic = epics.get(epicId);
        updateEpicStatus(epicId);
        updateEpicDuration(epic);
    }

    private void addPriorityTask(Task task) {
        final LocalDateTime startTime = task.getStartTime();
        final LocalDateTime endTime = task.getEndTime();
        for (Task t : prioritizedTasks) {
            final LocalDateTime existStart = t.getStartTime();
            final LocalDateTime existEnd = t.getEndTime();
            if (!endTime.isAfter(existStart)) {
                continue;
            }
            if (!existEnd.isAfter(startTime)) {
                continue;
            }

            throw new ManagerSaveException("Задача пересекаются с id=" + t.getId() + " c " + existStart + " по " + existEnd);
        }

        prioritizedTasks.add(task);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}
