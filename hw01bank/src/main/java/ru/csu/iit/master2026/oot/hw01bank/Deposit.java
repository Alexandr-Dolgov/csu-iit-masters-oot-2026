package ru.csu.iit.master2026.oot.hw01bank;

/** Пополнение счёта. Отмена снимает те же деньги — если они ещё на счёте. */
public final class Deposit implements Command {

    public Deposit(Account account, Amount amount) {
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
