package task.manager.schedule.service.inMemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import task.manager.schedule.model.Task;
import task.manager.schedule.service.HistoryManager;

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

    private static final Map<Integer, Node> history = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {// добавление Task

        if (task == null) {
            return;
        }else {
            remove(task.getId());
            linkLast(task);
            history.put(task.getId(), last);

        }
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
        final Node node = history.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    private void removeNode(Node node) {

        if (node.prev != null) {
            node.prev.next = node.next;
            if (node.next == null) {
                last = node.prev;
            } else {
                node.next.prev = node.prev;
            }
        } else {
            first = node.next;
            if (first == null) {
                last = null;
            } else {
                first.prev = null;
            }
        }
    }

    private void linkLast(Task task) {// двигаем историю
        final Node lastLink = last;
        final Node newNode = new Node(lastLink, task, null);
        last = newNode;
        if (lastLink == null) {
            first = newNode;
        } else {
            lastLink.next = newNode;
        }
    }
}