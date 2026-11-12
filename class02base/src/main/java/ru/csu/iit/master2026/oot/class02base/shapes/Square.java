package ru.csu.iit.master2026.oot.class02base.shapes;

/** Квадрат — сосед прямоугольника, а не наследник: обещаний Rectangle он не даёт. */
public record Square(int side) implements Shape {

    public Rectangle toRectangle() {
        return new Rectangle(side, side);
    }

    @Override
    public int area() {
        return side * side;
    }
}
