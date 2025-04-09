package task.manager.schedule.server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import task.manager.schedule.exception.ManagerSaveException;
import task.manager.schedule.exception.NotFoundException;
import task.manager.schedule.model.Epic;
import task.manager.schedule.model.Subtask;
import task.manager.schedule.model.Task;
import task.manager.schedule.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpHandler implements com.sun.net.httpserver.HttpHandler {
    TaskManager manager;
    TaskHandler taskHandler;
    EndpointHandler endpointHandler;
    public HttpHandler(TaskManager manager) {
        this.manager = manager;
        this.taskHandler = new TaskHandler();
        this.endpointHandler = new EndpointHandler();
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        Endpoint endpoint = endpointHandler.getEndpoint(h.getRequestURI().getPath(), h.getRequestMethod());
        final String path = h.getRequestURI().getPath();
        switch (endpoint) {
            case GET_TASKS:
                if (path.equals("/tasks")) {
                    sendText(h, gson.toJson(manager.getTasks().toString()));
                    break;
                }else if(path.equals("/epics")) {
                    sendText(h, gson.toJson(manager.getEpics().toString()));
                    break;
                }
            case GET_BY_ID:
                try {
                    int id = Integer.parseInt(h.getRequestURI().getPath().split("/")[2]);
                    if(path.equals("/tasks/"+ id)){
                        sendText(h, gson.toJson(manager.getTask(id).toString()));
                        break;
                    }else if(path.equals("/epics/"+ id)){
                        sendText(h, gson.toJson(manager.getEpic(id).toString()));
                        break;
                    }else if(path.equals("/subtasks/"+ id)){
                        sendText(h, gson.toJson(manager.getSubtask(id).toString()));
                        break;
                    }
                } catch (NotFoundException e) {
                    sendNotFound(h, "Данной задачи не существует по id: " + h.getRequestURI().getPath().split("/")[2]);
                    break;
                }
            case GET_EPICS_ID_SUBTASKS:
                try {
                    sendText(h, gson.toJson(manager.getEpicSubtasks(Integer.parseInt(h.getRequestURI().getPath().split("/")[2])).toString()));
                    break;
                } catch (NotFoundException e) {
                    sendNotFound(h, "Данного эпика не существует по id: " + e.getMessage());
                    break;
                }
            case GET_HISTORY:
                sendText(h, gson.toJson(manager.getHistory().toString()));
                break;
            case GET_PRIORITIZED:
                sendText(h, gson.toJson(manager.getPrioritizedTasks().toString()));
                break;
            case POST:
                convertTask(h);
                break;
            case DELETE_BY_ID:
                try {
                    int id = Integer.parseInt(h.getRequestURI().getPath().split("/")[2]);
                    if(path.equals("/tasks/"+id)) {
                        manager.deleteTask(id);
                        sendText(h,"");
                        break;
                    } else if(path.equals("/epics/"+id)) {
                        manager.deleteEpic(id);
                        sendText(h,"");
                        break;
                    }else if(path.equals("/subtasks/"+id)) {
                        manager.deleteSubtask(id);
                        sendText(h,"");
                        break;
                    }
                } catch (NotFoundException e) {
                    sendNotFound(h, "Данной задачи не существует по id: " + e.getMessage());
                    break;
                }
            default:
                errorServer(h);
                break;
        }
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        try (h) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(200, resp.length);
            h.getResponseBody().write(resp);
        }
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        try (h) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(404, resp.length);
            h.getResponseBody().write(resp);
        }
    }

    protected void updateText(HttpExchange h) throws IOException {
        try (h) {
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(201, 0);
        }
    }
    protected void errorServer(HttpExchange h) throws IOException {
        try (h) {
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(500, 0);
        }
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        try (h) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(406, resp.length);
            h.getResponseBody().write(resp);
        }
    }


    private void convertTask(HttpExchange h) throws IOException {
        InputStream inputStream = h.getRequestBody();
        final String path = h.getRequestURI().getPath();
        final String[] body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).split(",");
        Task task = taskHandler.taskFromString(body);
        if (path.equals("/tasks")) {
            if (body[0].split("=")[0].equals("id")) {
                try {
                    manager.updateTask(task);
                    updateText(h);
                } catch (ManagerSaveException e) {
                    sendHasInteractions(h, e.getMessage());
                }
            } else {
                try {
                    manager.createTask(task);
                    updateText(h);
                } catch (ManagerSaveException e) {
                    sendHasInteractions(h, e.getMessage());
                }
            }
        }
        if (path.equals("/epics")) {
            if (body[0].split("=")[0].equals("id")) {
                int taskId = Integer.parseInt(body[0].split("=")[1]);
                try {
                    manager.updateEpic(new Epic(task.getId(), task.getName(), task.getDescription(), task.getStatus(), task.getStartTime(), task.getDuration()));
                    updateText(h);
                } catch (ManagerSaveException e) {
                    sendHasInteractions(h, e.getMessage());
                }
            } else {
                try {
                    manager.createEpic(new Epic(task.getName(), task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration()));
                    updateText(h);
                } catch (ManagerSaveException e) {
                    sendHasInteractions(h, e.getMessage());
                }
            }
        }
        if (path.equals("/subtasks")) {
            final int epicId = Integer.parseInt(body[6].split("=")[1]);
            if (body[0].split("=")[0].equals("id")) {
                int taskId = Integer.parseInt(body[0].split("=")[1]);
                try {
                    manager.updateSubtask(new Subtask(task.getId(), task.getName(), task.getDescription(), task.getStatus(), task.getStartTime(), task.getDuration(), epicId));
                    updateText(h);
                } catch (ManagerSaveException e) {
                    sendHasInteractions(h, e.getMessage());
                }
            } else {
                try {
                    manager.createSubtask(new Subtask(task.getName(), task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration(), epicId));
                    updateText(h);
                } catch (ManagerSaveException e) {
                    sendHasInteractions(h, e.getMessage());
                }
            }
        }
    }
}
