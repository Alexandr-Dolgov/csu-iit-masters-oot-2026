package ru.csu.iit.master2026.oot.class02orderflow.domain;

import java.util.Objects;

public record CustomerId(String value) {

    public CustomerId {
        Objects.requireNonNull(value);
        if (value.isBlank()) {
            throw new IllegalArgumentException("Пустой идентификатор клиента");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
