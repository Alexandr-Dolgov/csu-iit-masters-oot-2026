package ru.csu.iit.master2026.oot.class02base.jdk;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Подстановка в самом JDK: где она держится, а где — нет. */
class JdkSubstitutionTest {

    /** Клиент, написанный в терминах {@link List}: по типу ему разрешено добавлять. */
    private static void addDefault(List<String> tags) {
        tags.add("new");
    }

    @Test
    void arrayList_подставляется_за_list() {
        List<String> tags = new ArrayList<>(List.of("sale"));

        addDefault(tags);

        assertThat(tags).containsExactly("sale", "new");
    }

    @Test
    void list_of_не_подставляется_за_list() {
        // add — «optional operation»: контракт List заранее разрешил реализации отказаться
        assertThatThrownBy(() -> addDefault(List.of("sale")))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> addDefault(Collections.unmodifiableList(new ArrayList<>())))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @SuppressWarnings("UseOfObsoleteCollectionType")
    void stack_extends_vector_отдаёт_чужие_операции() {
        Stack<String> stack = new Stack<>();
        stack.push("a");
        stack.push("b");

        stack.add(0, "под низ");   // метод Vector: стек так уметь не должен

        assertThat(stack.pop()).isEqualTo("b");
        assertThat(stack.firstElement()).isEqualTo("под низ");
    }

    @Test
    void deque_даёт_только_операции_стека() {
        Deque<String> stack = new ArrayDeque<>();
        stack.push("a");
        stack.push("b");

        assertThat(stack.pop()).isEqualTo("b");
    }

    @Test
    void timestamp_extends_date_ломает_симметричность_equals() {
        long now = 1_700_000_000_000L;
        Date date = new Date(now);
        Timestamp timestamp = new Timestamp(now);

        assertThat(date.equals(timestamp)).isTrue();
        assertThat(timestamp.equals(date)).isFalse();
    }
}
