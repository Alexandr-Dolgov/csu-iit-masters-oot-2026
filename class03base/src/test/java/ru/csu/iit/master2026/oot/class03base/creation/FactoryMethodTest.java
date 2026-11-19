package ru.csu.iit.master2026.oot.class03base.creation;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FactoryMethodTest {

    private final List<Line> lines = List.of(new Line("Чайник", 2490), new Line("Кружка", 350));

    @Test
    void скелет_один_продукт_выбирает_подкласс() {
        assertThat(new CsvExporter().export(lines)).isEqualTo("""
                Чайник;2490
                Кружка;350
                """);
        assertThat(new TextExporter().export(lines)).isEqualTo("""
                1. Чайник — 2490 руб.
                2. Кружка — 350 руб.
                """);
    }

    @Test
    void на_каждую_выгрузку_новый_продукт() {
        Exporter exporter = new TextExporter();

        exporter.export(lines);

        // нумерация не продолжилась: форматтер создан заново
        assertThat(exporter.export(lines)).startsWith("1. Чайник");
    }

    /** Клиент знает только Collection; какой итератор создать, решает коллекция. */
    private static int count(Collection<String> items) {
        int count = 0;
        for (Iterator<String> it = items.iterator(); it.hasNext(); it.next()) {
            count++;
        }
        return count;
    }

    @Test
    void iterator_фабричный_метод_коллекции() {
        List<String> list = new ArrayList<>(List.of("a", "b"));
        HashSet<String> set = new HashSet<>(List.of("a", "b"));
        ArrayDeque<String> deque = new ArrayDeque<>(List.of("a", "b"));

        assertThat(count(list)).isEqualTo(2);
        assertThat(count(set)).isEqualTo(2);
        assertThat(count(deque)).isEqualTo(2);

        // три коллекции — три разных класса итератора
        assertThat(List.of(list.iterator().getClass(), set.iterator().getClass(),
                deque.iterator().getClass())).doesNotHaveDuplicates();
    }
}
