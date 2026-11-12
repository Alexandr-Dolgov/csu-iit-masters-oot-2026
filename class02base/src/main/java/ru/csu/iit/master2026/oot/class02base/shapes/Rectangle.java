package ru.csu.iit.master2026.oot.class02base.shapes;

/** Прямоугольник с изменяемыми сторонами. */
public class Rectangle {

    private int width;
    private int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /** Постусловие: ширина стала {@code width}, высота не изменилась. */
    public void setWidth(int width) {
        this.width = width;
    }

    /** Постусловие: высота стала {@code height}, ширина не изменилась. */
    public void setHeight(int height) {
        this.height = height;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int area() {
        return width * height;
    }
}
