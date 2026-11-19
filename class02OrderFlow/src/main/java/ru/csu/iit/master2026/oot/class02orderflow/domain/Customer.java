package ru.csu.iit.master2026.oot.class02orderflow.domain;

import java.util.Objects;

/**
 * Клиент — сущность: равенство по идентификатору.
 *
 * <p>На занятии 1 был записью и сравнивался по всем полям: сменил телефон — стал другим клиентом.
 */
public final class Customer {

    private final CustomerId id;
    private final String name;
    private final Email email;
    private final String phone;

    public Customer(CustomerId id, String name, Email email, String phone) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
        this.phone = Objects.requireNonNull(phone);
    }

    public CustomerId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Email email() {
        return email;
    }

    public String phone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Customer other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Customer[" + id + ", " + name + "]";
    }
}
