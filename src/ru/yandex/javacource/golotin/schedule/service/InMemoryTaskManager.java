package ru.yandex.javacource.golotin.schedule.service;

import ru.yandex.javacource.golotin.schedule.model.Epic;
import ru.yandex.javacource.golotin.schedule.model.Status;
import ru.yandex.javacource.golotin.schedule.model.Subtask;
import ru.yandex.javacource.golotin.schedule.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {
    protected int counterId = 0;
    final Map<Integer, Task> tasks = new HashMap<>();
    final Map<Integer, Epic> epics = new HashMap<>();
    final Map<Integer, Subtask> subtasks = new HashMap<>();
    final HistoryManager historyManager = Manager.getDefaultHistory();

    @Override
    public Task createTask(Task task) {// создание Task
        final int id = ++counterId;
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {// создание Epic
        final int id = ++counterId;
        epic.setId(id);
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
        final int id = ++counterId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epicId);
        return subtask;
    }

    @Override
    public void updateTask(Task task) {// обновление Task
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    @Override
    public void updateEpic(Epic epic) {// обновление Epic
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {// обновление Subtask
        final int id = subtask.getId();
        final int epicId = subtask.getEpicId();
        final Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        subtasks.put(id, subtask);
        updateEpicStatus(epicId);// обновление статуса у Epic
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
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }// получаем список Tasks

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }// получаем список Epics

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {// получаем список Epic с Subtasks
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> getSubtasks = null;
        for (Integer subtaskId : epic.getSubtaskIds()) {
            if (this.subtasks.containsKey(subtaskId)) {
                getSubtasks.add(this.subtasks.get(subtaskId));
            }
        }
        return getSubtasks;
    }

    @Override
    public Task getTask(int id) {// получаем Task по id
        final Task task = tasks.get(id);
        historyManager.add(task);
        return task;

    }

    @Override
    public Epic getEpic(int id) {// получаем Epic по id
        final Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {// получаем Subtask по id
        final Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getHistory() {// получаем список истории
        return historyManager.getAll();
    }

    private void updateEpicStatus(int epicId) {// обновление статуса Epic
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            if (this.subtasks.containsKey(subtaskId)) {
                subtasks.add(this.subtasks.get(subtaskId));
            }
        }
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

}
