package ru.csu.iit.master2026.oot.class02base.equality;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColorPointTest {

    private final Point plain = new Point(1, 2);
    private final ColorPoint red = new ColorPoint(1, 2, "red");
    private final ColorPoint blue = new ColorPoint(1, 2, "blue");

    @Test
    void симметричность_восстановлена() {
        assertThat(plain.equals(red)).isTrue();
        assertThat(red.equals(plain)).isTrue();
    }

    @Test
    void транзитивность_нарушена() {
        assertThat(red.equals(plain)).isTrue();
        assertThat(plain.equals(blue)).isTrue();
        assertThat(red.equals(blue)).isFalse();
    }
}
