package ru.csu.iit.master2026.oot.class02base.shapes;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RectangleTest {

    @Test
    void растянуть_прямоугольник_по_колонке() {
        Rectangle block = new Rectangle(2, 5);

        Layout.fitToColumn(block, 10);

        assertThat(block.area()).isEqualTo(50);
    }

    @Test
    void растянуть_квадрат_по_колонке() {
        Rectangle block = new Square(5);

        Layout.fitToColumn(block, 10);

        // Клиент ждал 10 × 5 = 50: высоту он не трогал. Компилятор доволен, контракт нарушен.
        assertThat(block.area()).isEqualTo(100);
        assertThat(block.height()).isEqualTo(10);
    }
}
