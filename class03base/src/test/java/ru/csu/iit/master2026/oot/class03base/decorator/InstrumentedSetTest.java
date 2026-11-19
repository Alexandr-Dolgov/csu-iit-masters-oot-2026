package ru.csu.iit.master2026.oot.class03base.decorator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InstrumentedSetTest {

    @Test
    void наследник_считает_add_all_дважды() {
        InstrumentedHashSet<String> tags = new InstrumentedHashSet<>();

        tags.addAll(List.of("sale", "new", "hit"));

        // ждали 3: HashSet.addAll внутри вызывает add — а add переопределён
        assertThat(tags.addCount()).isEqualTo(6);
    }
}
