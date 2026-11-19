package ru.csu.iit.master2026.oot.class02base.equality;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PointTest {

    @Test
    void запись_равна_по_компонентам() {
        assertThat(new Point(1, 2)).isEqualTo(new Point(1, 2));
        assertThat(new Point(1, 2)).hasSameHashCodeAs(new Point(1, 2));
    }

    @Test
    void hashSet_находит_равную_точку() {
        Set<Point> visited = new HashSet<>();
        visited.add(new Point(1, 2));

        assertThat(visited.contains(new Point(1, 2))).isTrue();
    }

    @Test
    void toString_тоже_по_компонентам() {
        assertThat(new Point(1, 2)).hasToString("Point[x=1, y=2]");
    }
}
