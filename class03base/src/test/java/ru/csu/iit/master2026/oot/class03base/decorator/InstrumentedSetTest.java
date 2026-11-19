package ru.csu.iit.master2026.oot.class03base.decorator;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

class InstrumentedSetTest {

    @Test
    void наследник_считает_add_all_дважды() {
        InstrumentedHashSet<String> tags = new InstrumentedHashSet<>();

        tags.addAll(List.of("sale", "new", "hit"));

        // ждали 3: HashSet.addAll внутри вызывает add — а add переопределён
        assertThat(tags.addCount()).isEqualTo(6);
    }

    @Test
    void декоратор_считает_верно() {
        InstrumentedSet<String> tags = new InstrumentedSet<>(new HashSet<>());

        tags.addAll(List.of("sale", "new", "hit"));

        // addAll вложенного множества зовёт свой add, а не наш
        assertThat(tags.addCount()).isEqualTo(3);
    }

    @Test
    void декоратор_оборачивает_любое_множество() {
        Set<String> sorted = new InstrumentedSet<>(new TreeSet<>(List.of("b", "a")));

        sorted.add("c");

        assertThat(sorted).containsExactly("a", "b", "c");
    }
}
