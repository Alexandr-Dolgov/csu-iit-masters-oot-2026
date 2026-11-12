package ru.csu.iit.master2026.oot.class02base.equality;

import java.util.Objects;

/** Цветная точка содержит точку, а не является ею. Нужна точка — попросите её явно. */
public record ColorPoint(Point point, String color) {

    public ColorPoint {
        Objects.requireNonNull(point);
        Objects.requireNonNull(color);
    }

    public ColorPoint(int x, int y, String color) {
        this(new Point(x, y), color);
    }
}
