package ru.csu.iit.master2026.oot.class04base.ioc;

/** Выписывает счета. Счётчик сам не создаёт и не ищет — получает через конструктор. */
public final class Invoices {

    private final String prefix;
    private final Sequence numbers;

    public Invoices(String prefix, Sequence numbers) {
        this.prefix = prefix;
        this.numbers = numbers;
    }

    public String issue() {
        return prefix + "-" + numbers.next();
    }
}
