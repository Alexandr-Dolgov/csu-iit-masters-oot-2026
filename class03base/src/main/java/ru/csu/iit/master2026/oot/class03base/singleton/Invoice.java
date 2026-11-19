package ru.csu.iit.master2026.oot.class03base.singleton;

/** Счёт. По конструктору не видно, что он зависит от глобального счётчика. */
public final class Invoice {

    private final String number;

    public Invoice() {
        this.number = "INV-" + Sequence.getInstance().next();
    }

    public String number() {
        return number;
    }
}
