package ru.csu.iit.master2026.oot.hw01bank;

/**
 * Перевод между счетами. Атомарен: и выполнение, и отмена либо меняют оба счёта, либо ни одного.
 */
public final class Transfer implements Command {

    /** @throws IllegalArgumentException если {@code from} и {@code to} — один и тот же счёт */
    public Transfer(Account from, Account to, Amount amount) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("TODO");
    }
}
