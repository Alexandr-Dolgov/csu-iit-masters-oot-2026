package ru.csu.iit.master2026.oot.class02orderflow.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.csu.iit.master2026.oot.class02orderflow.domain.Money.rubles;

class MoneyTest {

    @Test
    void double_теряет_копейки() {
        assertThat(3490 * 1.03).isNotEqualTo(3594.70);
    }

    @Test
    void money_не_теряет_копейки() {
        assertThat(rubles(3490).plus(new Money(10_470))).isEqualTo(new Money(359_470));
    }

    @Test
    void страховка_3_процента_точно_до_копейки() {
        Money goods = rubles(3490);

        assertThat(goods.plus(goods.percent(3))).isEqualTo(new Money(359_470));
    }

    @Test
    void процент_округляется_до_копейки() {
        assertThat(new Money(150).percent(1)).isEqualTo(new Money(2));
    }

    @Test
    void вычесть_больше_чем_есть_нельзя() {
        assertThatThrownBy(() -> rubles(300).minus(rubles(500)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void отрицательную_сумму_создать_нельзя() {
        assertThatThrownBy(() -> new Money(-500))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void равенство_по_значению() {
        assertThat(rubles(100)).isEqualTo(new Money(10_000));
    }
}
