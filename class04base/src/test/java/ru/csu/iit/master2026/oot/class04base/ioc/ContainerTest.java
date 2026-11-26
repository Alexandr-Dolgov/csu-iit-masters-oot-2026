package ru.csu.iit.master2026.oot.class04base.ioc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContainerTest {

    /** Сборка: единственное место, где названы классы и связи между ними. */
    private static Container assemble() {
        Container container = new Container();
        container.register(Sequence.class, c -> new Sequence());
        container.register(Invoices.class, c -> new Invoices("INV", c.get(Sequence.class)));
        return container;
    }

    @Test
    void без_контейнера_сборка_вручную() {
        Sequence numbers = new Sequence();
        Invoices invoices = new Invoices("INV", numbers);

        assertThat(invoices.issue()).isEqualTo("INV-1");
    }

    @Test
    void контейнер_создаёт_объект_и_его_зависимости() {
        Invoices invoices = assemble().get(Invoices.class);

        assertThat(invoices.issue()).isEqualTo("INV-1");
        assertThat(invoices.issue()).isEqualTo("INV-2");
    }

    @Test
    void один_экземпляр_на_тип_решение_контейнера() {
        Container container = assemble();

        assertThat(container.get(Invoices.class)).isSameAs(container.get(Invoices.class));
        assertThat(container.get(Sequence.class)).isSameAs(container.get(Sequence.class));
    }

    @Test
    void объекты_создаются_по_первому_запросу_зависимости_раньше() {
        Container container = assemble();
        assertThat(container.created()).isEmpty();

        container.get(Invoices.class);

        assertThat(container.created()).containsExactly("Sequence", "Invoices");
    }

    @Test
    void у_каждого_теста_свой_контейнер_и_свой_счётчик() {
        // одиночка из занятия 3 связывал тесты; здесь «один на всех» — только внутри одной сборки
        assertThat(assemble().get(Invoices.class).issue()).isEqualTo("INV-1");
        assertThat(assemble().get(Invoices.class).issue()).isEqualTo("INV-1");
    }

    @Test
    void незарегистрированный_тип_ошибка_сборки() {
        assertThatThrownBy(() -> new Container().get(Invoices.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invoices");
    }
}
