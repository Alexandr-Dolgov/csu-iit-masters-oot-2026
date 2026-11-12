package ru.csu.iit.master2026.oot.class02orderflow.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.csu.iit.master2026.oot.class02orderflow.domain.Money.rubles;

class DeliveryTariffTest {

    @Test
    void курьер_платный_до_5000() {
        assertThat(new CourierDelivery().cost(rubles(4999))).isEqualTo(rubles(300));
    }

    @Test
    void курьер_бесплатный_от_5000() {
        assertThat(new CourierDelivery().cost(rubles(5000))).isEqualTo(Money.ZERO);
    }

    @Test
    void экспресс_500_на_следующий_день() {
        var express = new ExpressDelivery();

        assertThat(express.cost(rubles(64990))).isEqualTo(rubles(500));
        assertThat(express.days()).isEqualTo(1);
    }
}
