package ru.csu.iit.master2026.oot.class02base.equality;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ColorPointTest {

    private final ColorPoint red = new ColorPoint(1, 2, "red");

    @Test
    void цветные_точки_равны_по_координатам_и_цвету() {
        assertThat(red).isEqualTo(new ColorPoint(1, 2, "red"));
        assertThat(red).isNotEqualTo(new ColorPoint(1, 2, "blue"));
    }

    @Test
    void с_точкой_не_сравнивается_вовсе() {
        assertThat(red.equals(new Point(1, 2))).isFalse();
        assertThat(new Point(1, 2).equals(red)).isFalse();
    }

    @Test
    void нужна_точка_берём_точку() {
        Set<Point> grid = Set.of(new Point(0, 0), new Point(1, 2));

        assertThat(grid.contains(red.point())).isTrue();
    }
}
