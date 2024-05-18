package ru.yandex.javacource.golotin.schedule.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.javacource.golotin.schedule.converter.TaskConverter;
import ru.yandex.javacource.golotin.schedule.exception.ManagerSaveException;
import ru.yandex.javacource.golotin.schedule.model.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    HashMap<TaskType, TaskConverter> converters;
    private final File file;
    public FileBackedTaskManager() {
        this(Manager.getDefaultHistory());
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        this(historyManager, new File(TASK_CSV));
    }

    public FileBackedTaskManager(File file) {
        this(Manager.getDefaultHistory(), file);
    }


    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public void initialization() {
        loadFromFile();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.initialization();
        return manager;
    }
    @Override
    public List<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        saveToFile();
        return newTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
    }

    @Override
    public void updateSubtask(Subtask subTask) {
        super.updateSubtask(subTask);
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
    }

    private String toString(Task task) {
        return STR."\{task.getId()},\{task.getName()},\{task.getDescription()},\{task.getEpicId()}";
    }

    private Task fromString(String value) {
        final String[] columns = value.split(",");
        int id = 0;
        int epicId = 0;
        String name = "";
        String description = "";
        Status status = null;
        TaskType type = TaskType.valueOf(columns[1]);
        Task task = null;
        switch (type) {
            case TASK:
                task = new Task(name, status, description);
                break;
            case SUBTASK:
                task = new Subtask(name, status, description, epicId);
                break;
            case EPIC:
                task = new Epic(name, status, description);
                break;
        }
        return task;
    }

    static String toString(HistoryManager manager) {
        String sb = "";
        manager.getAll();
        return sb;
    }

    static List<Integer> historyFromString(String value) {
        final String[] ids = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String id : ids) {
            history.add(Integer.valueOf(id));
        }
        return history;
    }


    private void saveToFile() {// Сохранение в файл
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException(STR."Ошибка в файле: \{file.getAbsolutePath()}", e);
        }

    }

    private void loadFromFile() {// Чтение из в файла
        int maxId = 0;
        try (final BufferedReader reader = new BufferedReader(new FileReader(file, UTF_8))) {
            reader.readLine(); // Пропускаем заголовок
            while (true) {
                String line = reader.readLine();
                final Task task = fromString(line);
                final int id = task.getId();
                if (task.getType() == TaskType.TASK) {
                    tasks.put(id, task);
                }
                if (maxId < id) {
                    maxId = id;
                }
                if (line.isEmpty()) {
                    break;
                }
            }
            String line = reader.readLine();// История
        } catch (IOException e) {// Отлавливаем ошибки
            throw new ManagerSaveException(STR."Ошибка при чтении файла: \{file.getAbsolutePath()}", e);
        }
        counterId = maxId;// генератор
    }

    public static final String TASK_CSV = "task.csv";
}
