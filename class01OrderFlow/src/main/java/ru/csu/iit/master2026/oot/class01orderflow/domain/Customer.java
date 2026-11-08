package ru.csu.iit.master2026.oot.class01orderflow.domain;

import java.util.Objects;

/** Клиент. Данные для связи — значимые объекты, а не строки. */
public record Customer(CustomerId id, String name, Email email, String phone) {

    public Customer {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(email);
        Objects.requireNonNull(phone);
    }
}
