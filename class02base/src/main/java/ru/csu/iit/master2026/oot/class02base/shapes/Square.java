package ru.csu.iit.master2026.oot.class02base.shapes;

/**
 * Квадрат — это прямоугольник? По геометрии да. По контракту {@link Rectangle} — нет:
 * чтобы остаться квадратом, он меняет обе стороны сразу.
 */
public class Square extends Rectangle {

    public Square(int side) {
        super(side, side);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        super.setHeight(width);
    }

    @Override
    public void setHeight(int height) {
        super.setWidth(height);
        super.setHeight(height);
    }
}
