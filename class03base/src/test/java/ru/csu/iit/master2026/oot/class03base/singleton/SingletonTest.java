package ru.csu.iit.master2026.oot.class03base.singleton;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;

/** Два одинаковых теста — разные ответы: одиночка переживает тест и несёт состояние в следующий. */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SingletonTest {

    @Test
    @Order(1)
    void первый_счёт_получает_номер_1() {
        assertThat(new Invoice().number()).isEqualTo("INV-1");
    }

    @Test
    @Order(2)
    void тот_же_тест_получает_номер_2() {
        // ждали INV-1: результат зависит от того, какие тесты прошли раньше
        assertThat(new Invoice().number()).isEqualTo("INV-2");
    }
}
