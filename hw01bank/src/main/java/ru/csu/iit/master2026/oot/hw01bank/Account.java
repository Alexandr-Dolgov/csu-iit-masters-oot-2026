package ru.csu.iit.master2026.oot.hw01bank;

/**
 * Счёт клиента — сущность: равенство по номеру, баланс меняется.
 *
 * <p>Два вида счетов:
 * <ul>
 *   <li>дебетовый — баланс никогда не меньше нуля;</li>
 *   <li>кредитный с лимитом — баланс никогда не меньше {@code -limit}.</li>
 * </ul>
 *
 * <p>Как устроить два вида внутри — решать вам; снаружи доступны только фабрики ниже.
 * Добавлять поля, классы и интерфейсы можно, менять публичные сигнатуры — нельзя.
 */
public final class Account {

    /** Дебетовый счёт с нулевым балансом. */
    public static Account debit(AccountId id) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Кредитный счёт с нулевым балансом и лимитом {@code limit}. */
    public static Account credit(AccountId id, Amount limit) {
        throw new UnsupportedOperationException("TODO");
    }

    public AccountId id() {
        throw new UnsupportedOperationException("TODO");
    }

    /** Баланс в копейках; отрицательный — только у кредитного счёта. */
    public long balance() {
        throw new UnsupportedOperationException("TODO");
    }

    /** Сколько можно снять прямо сейчас, не нарушив инвариант счёта. */
    public Amount available() {
        throw new UnsupportedOperationException("TODO");
    }

    public void deposit(Amount amount) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * @throws IllegalStateException если {@code amount} больше {@link #available()};
     *                               баланс при этом не меняется
     */
    public void withdraw(Amount amount) {
        throw new UnsupportedOperationException("TODO");
    }
}
