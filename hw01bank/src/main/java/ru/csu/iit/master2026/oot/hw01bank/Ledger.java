package ru.csu.iit.master2026.oot.hw01bank;

/** Журнал операций: выполняет команды и отменяет последнюю выполненную. */
public final class Ledger {

    /** Выполняет команду и запоминает её. Если команда бросила исключение — она не запоминается. */
    public void run(Command command) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Отменяет последнюю выполненную команду и забывает её.
     * Если отмена бросила исключение — команда остаётся в журнале.
     *
     * @throws IllegalStateException если отменять нечего
     */
    public void undoLast() {
        throw new UnsupportedOperationException("TODO");
    }

    /** Сколько команд можно отменить. */
    public int size() {
        throw new UnsupportedOperationException("TODO");
    }
}
