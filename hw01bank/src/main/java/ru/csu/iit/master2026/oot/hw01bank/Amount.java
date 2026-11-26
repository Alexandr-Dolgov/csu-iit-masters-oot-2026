package ru.csu.iit.master2026.oot.hw01bank;

/**
 * Сумма операции в рублях, хранится в копейках.
 *
 * <p>Значимый объект: неизменяемый, равенство по значению, отрицательной суммы не бывает.
 * Класс можно превратить в запись — сигнатуры ниже при этом должны остаться.
 */
public final class Amount {

    public static final Amount ZERO = new Amount(0);

    /** @throws IllegalArgumentException если {@code kopecks < 0} */
    public Amount(long kopecks) {
        throw new UnsupportedOperationException("TODO");
    }

    public static Amount rubles(long rubles) {
        throw new UnsupportedOperationException("TODO");
    }

    public long kopecks() {
        throw new UnsupportedOperationException("TODO");
    }

    public Amount plus(Amount other) {
        throw new UnsupportedOperationException("TODO");
    }

    /** @throws IllegalArgumentException если результат меньше нуля */
    public Amount minus(Amount other) {
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isAtLeast(Amount other) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Например, {@code 1234.05 руб.} */
    @Override
    public String toString() {
        throw new UnsupportedOperationException("TODO");
    }
}
