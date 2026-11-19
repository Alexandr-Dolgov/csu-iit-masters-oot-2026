package ru.csu.iit.master2026.oot.class02orderflow.domain;

import java.util.Objects;

public record Email(String value) {

    public Email {
        Objects.requireNonNull(value);
        if (!value.contains("@")) {
            throw new IllegalArgumentException("Некорректный email: " + value);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
