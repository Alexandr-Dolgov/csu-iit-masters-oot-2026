package ru.csu.iit.master2026.oot.class01orderflow.domain;

import java.util.Objects;

public record OrderId(String value) {

    public OrderId {
        Objects.requireNonNull(value);
        if (value.isBlank()) {
            throw new IllegalArgumentException("Пустой идентификатор заказа");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
