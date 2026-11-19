package ru.csu.iit.master2026.oot.class03base.singleton;

/** Счёт. Зависимость от счётчика видна в конструкторе. */
public final class Invoice {

    private final String number;

    public Invoice(Sequence numbers) {
        this.number = "INV-" + numbers.next();
    }

    public String number() {
        return number;
    }
}
