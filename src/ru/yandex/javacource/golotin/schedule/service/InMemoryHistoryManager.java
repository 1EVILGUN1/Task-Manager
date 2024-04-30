package ru.yandex.javacource.golotin.schedule.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.javacource.golotin.schedule.model.Task;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {// присвоение prev, element, next
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    Map<Integer, Node> history = new HashMap<>();
    Node first;
    Node last;

    @Override
    public void add(Task task) {// добавление Task
        Node node = history.get(task.getId());
        removeNode(node);
        linkLast(task);
    }

    @Override
    public List<Task> getAll() {// вывод списка истории
        List<Task> list = new ArrayList<>();
        Node current = first;
        while (current != null) {

            current = current.next;
        }
        return list;
    }


    @Override
    public void remove(int id) {// удаление по id
        Node node = history.get(id);
        removeNode(node);
    }

    private void removeNode(Node node) {// Удаление из связанного списка
        history.remove(node.item.getId());
    }

    void linkLast(Task task) {// двигаем историю
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
    }
}