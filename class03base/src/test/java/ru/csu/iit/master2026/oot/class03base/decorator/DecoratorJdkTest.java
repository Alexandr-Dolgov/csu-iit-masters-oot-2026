package ru.csu.iit.master2026.oot.class03base.decorator;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** java.io собран из декораторов: каждый слой добавляет одно умение. */
class DecoratorJdkTest {

    @Test
    void цепочка_java_io() throws IOException {
        byte[] file = "Чайник\nКружка\n".getBytes(StandardCharsets.UTF_8);

        try (BufferedReader reader = new BufferedReader(          // + чтение строками
                new InputStreamReader(                            // адаптер: байты → символы
                        new ByteArrayInputStream(file),           // источник байтов
                        StandardCharsets.UTF_8))) {

            assertThat(reader.readLine()).isEqualTo("Чайник");
            assertThat(reader.readLine()).isEqualTo("Кружка");
        }
    }

    @Test
    void слой_сжатия_вставляется_без_правки_остальных() throws IOException {
        ByteArrayOutputStream file = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(file)) {
            gzip.write("Чайник".getBytes(StandardCharsets.UTF_8));
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new GZIPInputStream(                      // + распаковка
                                new ByteArrayInputStream(file.toByteArray())),
                        StandardCharsets.UTF_8))) {

            assertThat(reader.readLine()).isEqualTo("Чайник");
        }
    }

    @Test
    void unmodifiable_list_представление_а_не_копия() {
        List<String> tags = new ArrayList<>(List.of("sale"));
        List<String> view = Collections.unmodifiableList(tags);
        List<String> copy = List.copyOf(tags);   // из занятия 1

        assertThatThrownBy(() -> view.add("new"))
                .isInstanceOf(UnsupportedOperationException.class);

        tags.add("new");   // владелец меняет список — обёртка это видит, копия нет
        assertThat(view).containsExactly("sale", "new");
        assertThat(copy).containsExactly("sale");
    }
}
