package ru.csu.iit.master2026.oot.class03base.composite;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompositeTest {

    private final Item kettle = new Item("Чайник", 2490);
    private final Item mug = new Item("Кружка", 350);
    private final Bundle tea = new Bundle("Чайный набор", List.of(kettle, mug, mug), 10);
    private final Bundle gift = new Bundle("Подарок", List.of(tea, new Item("Открытка", 100)), 0);

    /** Клиент считает корзину, не различая товары и наборы. */
    private static int total(List<Product> basket) {
        return basket.stream().mapToInt(Product::price).sum();
    }

    @Test
    void набор_считается_как_товар() {
        assertThat(tea.price()).isEqualTo(2871);              // (2490 + 350 + 350) − 10%
        assertThat(gift.price()).isEqualTo(2971);             // набор в наборе
        assertThat(total(List.of(kettle, gift))).isEqualTo(5461);
    }

    /**
     * JUnit 5 сам компоновщик: DynamicContainer — узел, DynamicTest — лист.
     * Дерево тестов повторяет дерево товаров.
     */
    @TestFactory
    DynamicNode у_каждого_товара_положительная_цена() {
        return node(gift);
    }

    private static DynamicNode node(Product product) {
        if (product instanceof Bundle bundle) {
            return DynamicContainer.dynamicContainer(bundle.name(),
                    bundle.parts().stream().map(CompositeTest::node));
        }
        return DynamicTest.dynamicTest(product.name(),
                () -> assertThat(product.price()).isPositive());
    }
}
