package ru.csu.iit.master2026.oot.class02base.shapes;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RectangleTest {

    @Test
    void растянуть_прямоугольник_по_колонке() {
        Rectangle block = Layout.fitToColumn(new Rectangle(2, 5), 10);

        assertThat(block.area()).isEqualTo(50);
    }

    @Test
    void квадрат_растягивается_только_как_прямоугольник() {
        Rectangle block = Layout.fitToColumn(new Square(5).toRectangle(), 10);

        assertThat(block.area()).isEqualTo(50);
    }

    @Test
    void общий_контракт_фигур_площадь() {
        List<Shape> shapes = List.of(new Rectangle(2, 5), new Square(3));

        assertThat(shapes).extracting(Shape::area).containsExactly(10, 9);
    }
}
