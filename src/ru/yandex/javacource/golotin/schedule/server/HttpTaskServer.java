package ru.yandex.javacource.golotin.schedule.server;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.golotin.schedule.model.Status;
import ru.yandex.javacource.golotin.schedule.model.Task;
import ru.yandex.javacource.golotin.schedule.service.Manager;
import ru.yandex.javacource.golotin.schedule.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Manager.getDefault();

        taskManager.createTask(new Task("Дом", Status.NEW, "Убраться в кухни и ванной", LocalDateTime.now(),40));
        taskManager.createTask(new Task("Работа", Status.IN_PROGRESS, "Сделать куча рутины и пойти домой:)",LocalDateTime.now().plusDays(1),50));



        HttpHandler httpHandler = new HttpHandler(taskManager);

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks",httpHandler);
        server.createContext("/epics",httpHandler);
        server.createContext("/subtasks", httpHandler);
        server.createContext("/history", httpHandler);
        server.createContext("/prioritized", httpHandler);
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
}
