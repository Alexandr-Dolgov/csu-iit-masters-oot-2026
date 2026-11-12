package ru.csu.iit.master2026.oot.class02base.equality;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColorPointTest {

    private final Point plain = new Point(1, 2);
    private final ColorPoint red = new ColorPoint(1, 2, "red");

    @Test
    void симметричность_нарушена() {
        assertThat(plain.equals(red)).isTrue();    // Point сравнивает только координаты
        assertThat(red.equals(plain)).isFalse();   // ColorPoint требует ColorPoint
    }
}
