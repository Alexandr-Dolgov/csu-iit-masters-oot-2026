package ru.csu.iit.master2026.oot.class03base.adapter;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Адаптер: есть объект с одним интерфейсом, клиенту нужен другой. */
class AdapterJdkTest {

    @Test
    void input_stream_reader_байты_в_символы() throws IOException {
        // «Чай» в UTF-8 — шесть байт; клиенту нужны три символа
        InputStream bytes = new ByteArrayInputStream("Чай".getBytes(StandardCharsets.UTF_8));
        Reader chars = new InputStreamReader(bytes, StandardCharsets.UTF_8);

        char[] buffer = new char[10];
        int read = chars.read(buffer);

        assertThat(read).isEqualTo(3);
        assertThat(new String(buffer, 0, read)).isEqualTo("Чай");
    }

    @Test
    void arrays_as_list_массив_под_видом_списка() {
        String[] tags = {"sale", "new"};
        List<String> list = Arrays.asList(tags);

        // не копия: запись проходит прямо в массив
        list.set(0, "hit");
        assertThat(tags[0]).isEqualTo("hit");

        // у массива нет «добавить» — адаптер честно отказывается (optional operation, занятие 2)
        assertThatThrownBy(() -> list.add("top"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @SuppressWarnings("UseOfObsoleteCollectionType")
    void collections_list_старый_enumeration_в_новый_list() {
        Enumeration<String> legacy = new Vector<>(List.of("a", "b")).elements();

        List<String> list = Collections.list(legacy);

        assertThat(list).containsExactly("a", "b");
    }
}
