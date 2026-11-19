package ru.csu.iit.master2026.oot.class03base.composite;

import java.util.List;

/**
 * Паттерн «Компоновщик»: набор — тоже Product. Внутри могут быть товары
 * и другие наборы; цену считает рекурсивно, со своей скидкой.
 */
public record Bundle(String name, List<Product> parts, int discountPercent) implements Product {

    public Bundle {
        parts = List.copyOf(parts);
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Скидка от 0 до 100%: " + discountPercent);
        }
    }

    @Override
    public int price() {
        int sum = 0;
        for (Product part : parts) {
            sum += part.price();
        }
        return sum * (100 - discountPercent) / 100;
    }
}
