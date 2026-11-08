package ru.csu.iit.master2026.oot.class01orderflow.domain;

import java.util.Objects;

/** Позиция заказа: товар, количество, цена за единицу. */
public record OrderItem(String sku, int quantity, Money price) {

    public OrderItem {
        Objects.requireNonNull(sku);
        Objects.requireNonNull(price);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть положительным: " + sku);
        }
    }

    public Money subtotal() {
        return price.times(quantity);
    }
}
