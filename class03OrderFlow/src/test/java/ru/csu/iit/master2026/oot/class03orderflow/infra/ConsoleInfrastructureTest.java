package ru.csu.iit.master2026.oot.class03orderflow.infra;

import org.junit.jupiter.api.Test;
import ru.csu.iit.master2026.oot.class03orderflow.app.Infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

class ConsoleInfrastructureTest {

    @Test
    void семейство_согласовано_у_всех_сценариев_одно_хранилище() {
        Infrastructure infra = new ConsoleInfrastructure();

        assertThat(infra.orders()).isSameAs(infra.orders());
        assertThat(infra.notifier()).isSameAs(infra.notifier());
    }

    @Test
    void разные_фабрики_разные_хранилища() {
        assertThat(new ConsoleInfrastructure().orders())
                .isNotSameAs(new ConsoleInfrastructure().orders());
    }
}
