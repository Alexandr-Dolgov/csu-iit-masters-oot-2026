package ru.csu.iit.master2026.oot.class02base.equality;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ColorPointTest {

    private final Point plain = new Point(1, 2);
    private final ColorPoint red = new ColorPoint(1, 2, "red");
    private final ColorPoint blue = new ColorPoint(1, 2, "blue");

    @Test
    void с_getClass_контракт_equals_соблюдён() {
        assertThat(plain.equals(red)).isFalse();
        assertThat(red.equals(plain)).isFalse();
        assertThat(red.equals(blue)).isFalse();
        assertThat(red.equals(new ColorPoint(1, 2, "red"))).isTrue();
    }

    @Test
    void с_getClass_подстановка_нарушена() {
        Set<Point> grid = Set.of(new Point(0, 0), new Point(1, 2));

        // Клиент Point не знает про подклассы: точка (1, 2) в сетке есть, а он её не находит
        assertThat(grid.contains(new CountedPoint(1, 2))).isFalse();
    }
}
