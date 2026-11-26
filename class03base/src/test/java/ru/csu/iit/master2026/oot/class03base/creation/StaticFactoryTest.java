package ru.csu.iit.master2026.oot.class03base.creation;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/** Статический фабричный метод может то, чего не может конструктор. */
class StaticFactoryTest {

    @Test
    void list_of_сам_выбирает_класс_реализации() {
        List<Integer> two = List.of(1, 2);
        List<Integer> three = List.of(1, 2, 3);

        // new всегда даёт ровно тот класс, что написан; фабрика — любой подтип
        assertThat(two.getClass()).isNotEqualTo(three.getClass());
        assertThat(two.getClass().getSimpleName()).isEqualTo("List12");
        assertThat(three.getClass().getSimpleName()).isEqualTo("ListN");
    }

    @Test
    @SuppressWarnings({"removal", "NumberEquality"})
    void integer_value_of_отдаёт_готовый_объект_из_кэша() {
        // −128..127 кэшируются: фабрика не обязана создавать новый объект
        assertThat(Integer.valueOf(127) == Integer.valueOf(127)).isTrue();
        assertThat(Integer.valueOf(128) == Integer.valueOf(128)).isFalse();

        // конструктор обязан: new — всегда новый объект (и поэтому он устарел)
        assertThat(new Integer(127) == new Integer(127)).isFalse();
    }

    @Test
    void optional_empty_один_на_всех() {
        // пустое значение неизменяемо — хватает одного экземпляра
        assertThat(Optional.empty()).isSameAs(Optional.empty());
    }
}
