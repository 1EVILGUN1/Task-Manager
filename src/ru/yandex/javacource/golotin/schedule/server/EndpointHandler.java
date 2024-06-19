package ru.yandex.javacource.golotin.schedule.server;

public class EndpointHandler {
    public Endpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestPath.equals("/tasks") && requestMethod.equals("GET")) {
            return Endpoint.GET_TASKS;
        } else if (requestPath.equals("/tasks") && requestMethod.equals("POST")) {
            return Endpoint.POST;
        } else if (requestPath.equals("/tasks/" + requestPath.split("/")[2]) && requestMethod.equals("GET")) {
            return Endpoint.GET_BY_ID;
        } else if (requestPath.equals("/tasks/" + requestPath.split("/")[2]) && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_BY_ID;
        } else if (requestPath.equals("/epics") && requestMethod.equals("GET")) {
            return Endpoint.GET_TASKS;
        } else if (requestPath.equals("/epics") && requestMethod.equals("POST")) {
            return Endpoint.POST;
        } else if (requestPath.equals("epics/" + requestPath.split("/")[2]) && requestMethod.equals("GET")) {
            return Endpoint.GET_BY_ID;
        } else if (requestPath.equals("/epics/" + requestPath.split("/")[2]) && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_BY_ID;
        } else if (requestPath.equals("/epics/" + requestPath.split("/")[2] + "/subtasks") && requestMethod.equals("GET")) {
            return Endpoint.GET_EPICS_ID_SUBTASKS;
        } else if (requestPath.equals("/subtasks") && requestMethod.equals("GET")) {
            return Endpoint.GET_SUBTASKS;
        } else if (requestPath.equals("/subtasks") && requestMethod.equals("POST")) {
            return Endpoint.POST;
        } else if (requestPath.equals("/subtasks/" + requestPath.split("/")[2]) && requestMethod.equals("GET")) {
            return Endpoint.GET_BY_ID;
        } else if (requestPath.equals("subtasks/" + requestPath.split("/")[2]) && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_BY_ID;
        } else if (requestPath.endsWith("history") && requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        } else if (requestPath.endsWith("prioritized") && requestMethod.equals("GET")) {
            return Endpoint.GET_PRIORITIZED;
        } else {
            return Endpoint.UNKNOWN;
        }
    }
}
