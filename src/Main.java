import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.createTask(new Task("Дом", Status.NEW, "Убраться в кухни и ванной"));
        taskManager.createTask(new Task("Работа", Status.IN_PROGRESS, "Сделать куча рутины и пойти домой:)"));


        taskManager.createEpic(new Epic("Прогулка", Status.NEW, "Прежде чем погулять нужно:"));
        taskManager.createSubtask(new Subtask("Уборка", Status.NEW, "Убраться в квартире"));
        taskManager.createSubtask(new Subtask("Одежда", Status.NEW, "Подготовить одежду к прогулке"));

        taskManager.createEpic(new Epic("Приготовить кофе", Status.NEW, "Пойти на кухню и:"));
        taskManager.createSubtask(new Subtask("Сделать кофе", Status.NEW, "Налить в кружку горячую воду и наспать кофе"));

        taskManager.updateTask(new Task("Дом", Status.IN_PROGRESS, "Уборка в кухни и ванной"));

        taskManager.updateEpic(new Epic("Прогулка", Status.NEW, "Не пойду гулять"));
        taskManager.updateSubtask(new Subtask("Уборка", Status.IN_PROGRESS, "Убираюсь)"));
        taskManager.updateSubtask(new Subtask("Сделать кофе", Status.DONE, "Кофе приготовлено"));


        taskManager.getTasks();
        taskManager.getEpics();

        taskManager.deleteTask(1);
        taskManager.deleteEpic(1);
        taskManager.deleteSubtask(1);

        taskManager.cleanTasks();
        taskManager.cleanSubtasks();
        taskManager.cleanEpics();

    }
}