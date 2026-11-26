package ru.csu.iit.master2026.oot.class03base.singleton;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class SingletonTest {

    @Test
    void первый_счёт_получает_номер_1() {
        assertThat(new Invoice(new Sequence()).number()).isEqualTo("INV-1");
    }

    @Test
    void тот_же_тест_тоже_получает_номер_1() {
        // свой счётчик у каждого теста: порядок запуска больше не важен
        assertThat(new Invoice(new Sequence()).number()).isEqualTo("INV-1");
    }

    @Test
    void один_счётчик_на_всё_приложение_решение_сборки() {
        Sequence shared = new Sequence();

        new Invoice(shared);

        assertThat(new Invoice(shared).number()).isEqualTo("INV-2");
    }

    @Test
    void одиночка_без_состояния_безвреден() {
        assertThat(Rubles.FORMAT.format(350)).isEqualTo("350 руб.");
        assertThat(Rubles.valueOf("FORMAT")).isSameAs(Rubles.FORMAT);
    }

    @Test
    void одиночки_в_jdk() {
        // один процесс — одна среда выполнения
        assertThat(Runtime.getRuntime()).isSameAs(Runtime.getRuntime());
        // пустой неизменяемый список: состояния нет, хватает одного
        assertThat(Collections.emptyList()).isSameAs(Collections.emptyList());
    }
}
