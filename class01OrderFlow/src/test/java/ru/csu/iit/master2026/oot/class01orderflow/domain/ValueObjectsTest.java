package ru.csu.iit.master2026.oot.class01orderflow.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.csu.iit.master2026.oot.class01orderflow.domain.Money.rubles;

class ValueObjectsTest {

    @Test
    void стоимость_позиции_цена_на_количество() {
        assertThat(new OrderItem("Кружка", 2, rubles(350)).subtotal()).isEqualTo(rubles(700));
    }

    @Test
    void нулевое_количество_создать_нельзя() {
        assertThatThrownBy(() -> new OrderItem("Кружка", 0, rubles(350)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void email_без_собаки_создать_нельзя() {
        assertThatThrownBy(() -> new Email("anna.example.ru"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
