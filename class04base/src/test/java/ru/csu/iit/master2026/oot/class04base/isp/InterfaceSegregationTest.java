package ru.csu.iit.master2026.oot.class04base.isp;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterfaceSegregationTest {

    /** Бухгалтерия зависит только от печати. */
    private static List<String> printInvoices(Printer printer) {
        return List.of(printer.print("INV-1"), printer.print("INV-2"));
    }

    /** Архив — только от сканера: принтер сюда не передать, компилятор не даст. */
    private static String archive(Scanner scanner) {
        return scanner.scan("договор");
    }

    @Test
    void принтер_и_комбайн_подходят_бухгалтерии() {
        assertThat(printInvoices(new SimplePrinter()))
                .containsExactly("напечатано: INV-1", "напечатано: INV-2");
        assertThat(printInvoices(new MultiFunctionDevice()))
                .containsExactly("напечатано: INV-1", "напечатано: INV-2");
    }

    @Test
    void архиву_подходит_только_то_что_умеет_сканировать() {
        // archive(new SimplePrinter()) — не компилируется: ошибка на этапе сборки, а не у клиента
        assertThat(archive(new MultiFunctionDevice())).isEqualTo("отсканировано: договор");
    }

    @Test
    void узкий_интерфейс_легко_подменить_лямбдой() {
        List<String> printed = new ArrayList<>();
        Printer recording = document -> {
            printed.add(document);
            return "ok";
        };

        printInvoices(recording);

        assertThat(printed).containsExactly("INV-1", "INV-2");
    }

    /** Обойти может любой Iterable — не нужны ни size, ни add, ни remove из Collection. */
    private static int totalLength(Iterable<String> words) {
        int total = 0;
        for (String word : words) {
            total += word.length();
        }
        return total;
    }

    @Test
    void в_jdk_узкие_интерфейсы_iterable_comparable_autocloseable() {
        Iterable<String> two = () -> List.of("чай", "кофе").iterator();
        assertThat(totalLength(two)).isEqualTo(7);

        assertThat(Comparator.<String>naturalOrder().compare("а", "б")).isNegative();

        List<String> log = new ArrayList<>();
        try (AutoCloseable resource = () -> log.add("закрыт")) {
            log.add("работает");
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        assertThat(log).containsExactly("работает", "закрыт");
    }
}
