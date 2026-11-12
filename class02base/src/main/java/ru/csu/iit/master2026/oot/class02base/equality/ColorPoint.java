package ru.csu.iit.master2026.oot.class02base.equality;

import java.util.Objects;

/** Цветная точка: точка плюс новое значимое поле. Наследование кажется естественным. */
public class ColorPoint extends Point {

    private final String color;

    public ColorPoint(int x, int y, String color) {
        super(x, y);
        this.color = Objects.requireNonNull(color);
    }

    public String color() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ColorPoint cp && super.equals(cp) && color.equals(cp.color);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + color.hashCode();
    }
}
