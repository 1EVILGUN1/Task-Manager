package ru.yandex.javacource.golotin.schedule.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.*;

import java.nio.file.Files;

import java.util.Map;

import ru.yandex.javacource.golotin.schedule.exception.ManagerSaveException;
import ru.yandex.javacource.golotin.schedule.model.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String HEADER = "id,type,name,status,description,epic";
    private final File file;

    public FileBackedTaskManager(File file) {
        super(Manager.getDefaultHistory());
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            final String csv = Files.readString(file.toPath());
            final String[] lines = csv.split(System.lineSeparator());
            int generatorId = 0;
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    break;
                }
                final Task task = taskFromString(line);
                final int id = task.getId();
                if (id > generatorId) {
                    generatorId = id;
                }
                if (task.getType() == TaskType.TASK) {
                    taskManager.createTask(task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    taskManager.createSubtask(new Subtask(task.getId(), task.getName(), task.getDescription(),
                            task.getStatus(), task.getEpicId()));
                } else if (task.getType() == TaskType.EPIC) {
                    taskManager.createEpic(new Epic(task.getId(), task.getName(), task.getDescription(),
                            task.getStatus()));
                    for (Subtask subtask : taskManager.subtasks.values()) {// Поиск подзадач эпика
                        if (subtask.getEpicId() == task.getId()) {
                            Epic epic = taskManager.epics.get(task.getId());
                            epic.addSubtaskId(subtask.getId());
                        }
                    }
                }
            }
            for (Map.Entry<Integer, Subtask> e : taskManager.subtasks.entrySet()) {
                final Subtask subtask = e.getValue();
                final Epic epic = taskManager.epics.get(subtask.getEpicId());
                epic.addSubtaskId(subtask.getId());
            }
            taskManager.counterId = generatorId;
        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно прочитать файл: " + file.getName(), e);
        }
        return taskManager;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        saveToFile();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        saveToFile();
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        saveToFile();
        return newSubtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        saveToFile();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        saveToFile();
    }

    @Override
    public void updateSubtask(Subtask subTask) {
        super.updateSubtask(subTask);
        saveToFile();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        saveToFile();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        saveToFile();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        saveToFile();
    }

    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription() + "," + (task.getType().equals(TaskType.SUBTASK) ? task.getEpicId() : "");
    }


    public static Task taskFromString(String value) {
        final String[] values = value.split(",");
        final int id = Integer.parseInt(values[0]);
        final TaskType type = TaskType.valueOf(values[1]);
        final String name = values[2];
        final Status status = Status.valueOf(values[3]);
        final String description = values[4];
        if (type == TaskType.TASK) {
            return new Task(id, name, description, status);
        }
        if (type == TaskType.SUBTASK) {
            final int epicId = Integer.parseInt(values[5]);
            return new Subtask(id, name, description, status, epicId);
        }

        return new Epic(id, name, description, status);
    }

    protected void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            writer.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                final Task task = entry.getValue();
                writer.write(toString(task));
                writer.newLine();
            }

            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                final Task task = entry.getValue();
                writer.write(toString(task));
                writer.newLine();
            }

            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                final Task task = entry.getValue();
                writer.write(toString(task));
                writer.newLine();
            }

            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла: " + file.getName(), e);
        }
    }
}
