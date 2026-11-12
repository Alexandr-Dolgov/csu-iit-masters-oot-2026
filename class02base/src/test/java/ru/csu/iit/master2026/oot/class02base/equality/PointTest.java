package ru.csu.iit.master2026.oot.class02base.equality;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PointTest {

    @Test
    void равные_точки() {
        assertThat(new Point(1, 2)).isEqualTo(new Point(1, 2));
    }

    @Test
    void list_ищет_через_equals() {
        List<Point> visited = List.of(new Point(1, 2));

        assertThat(visited.contains(new Point(1, 2))).isTrue();
    }

    @Test
    void hashSet_теряет_равную_точку() {
        Set<Point> visited = new HashSet<>();
        visited.add(new Point(1, 2));

        // equals переопределён, hashCode — нет: HashSet ищет не в той корзине
        assertThat(visited.contains(new Point(1, 2))).isFalse();
    }
}
