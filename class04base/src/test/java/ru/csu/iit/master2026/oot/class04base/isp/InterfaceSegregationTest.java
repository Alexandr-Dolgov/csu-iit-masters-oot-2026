package ru.csu.iit.master2026.oot.class04base.isp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterfaceSegregationTest {

    /** Бухгалтерии нужна только печать, но в сигнатуре — весь Device. */
    private static List<String> printInvoices(Device device) {
        return List.of(device.print("INV-1"), device.print("INV-2"));
    }

    /** Архиву нужен только сканер. */
    private static String archive(Device device) {
        return device.scan("договор");
    }

    @Test
    void печать_через_толстый_интерфейс_работает() {
        assertThat(printInvoices(new SimplePrinter()))
                .containsExactly("напечатано: INV-1", "напечатано: INV-2");
    }

    @Test
    void компилятор_пропускает_принтер_в_архив_а_падает_он_во_время_выполнения() {
        // SimplePrinter — это Device, подстановка разрешена, но контракт scan не выполнен
        assertThatThrownBy(() -> archive(new SimplePrinter()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void та_же_беда_в_jdk_list_of_не_умеет_add() {
        // занятие 2: List требует add, неизменяемый список отказывается во время выполнения
        List<String> tags = List.of("sale");

        assertThatThrownBy(() -> tags.add("new"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
