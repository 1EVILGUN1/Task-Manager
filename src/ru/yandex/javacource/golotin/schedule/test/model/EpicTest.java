package ru.yandex.javacource.golotin.schedule.test.model;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.golotin.schedule.model.Epic;
import ru.yandex.javacource.golotin.schedule.model.Status;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void shouldEqualsWithCopy() {
        Epic epic = new Epic("name", Status.NEW, "desc");
        Epic epicExpected = new Epic("name1", Status.NEW, "desc");
        assertEquals(epicExpected, epic);

    }

}