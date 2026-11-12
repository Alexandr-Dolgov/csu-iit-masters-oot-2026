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

    /** С обычной точкой сравниваемся без цвета — ради симметричности. */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        if (!(o instanceof ColorPoint cp)) {
            return o.equals(this);
        }
        return super.equals(cp) && color.equals(cp.color);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + color.hashCode();
    }
}
