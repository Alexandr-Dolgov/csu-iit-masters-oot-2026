package ru.csu.iit.master2026.oot.class02base.shapes;

/** Неизменяемый прямоугольник: «поменять ширину» — значит получить новый прямоугольник. */
public record Rectangle(int width, int height) implements Shape {

    public Rectangle withWidth(int width) {
        return new Rectangle(width, height);
    }

    @Override
    public int area() {
        return width * height;
    }
}
