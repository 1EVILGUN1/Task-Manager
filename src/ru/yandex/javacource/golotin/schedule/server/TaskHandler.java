package ru.yandex.javacource.golotin.schedule.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.golotin.schedule.model.Status;
import ru.yandex.javacource.golotin.schedule.model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskHandler {
    public Task taskFromString(String[] body){
        if (body[0].split("=")[0].equals("id")) {
            int taskId = Integer.parseInt(body[0].split("=")[1]);
            final String name = body[1].split("=")[1];
            final String description = body[2].split("=")[1];
            final Status status = Status.valueOf(body[3].split("=")[1]);
            final LocalDateTime startTime = LocalDateTime.parse(body[4].split("=")[1]);
            final LocalDateTime endTime = LocalDateTime.parse(body[5].split("=")[1].replaceAll("}\"", "").trim());
            final Duration duration = Duration.between(startTime, endTime);
            return new Task(taskId, name, description, status, startTime, duration.toMinutesPart());
        } else {
            final String name = body[0].split("=")[1];
            final String description = body[1].split("=")[1];
            final Status status = Status.valueOf(body[2].split("=")[1]);
            final LocalDateTime startTime = LocalDateTime.parse(body[3].split("=")[1]);
            final LocalDateTime endTime = LocalDateTime.parse(body[4].split("=")[1].replaceAll("}\"", "").trim());
            final Duration duration = Duration.between(startTime, endTime);
            return new Task(name, status, description, startTime, duration.toMinutesPart());
        }
    }

}
