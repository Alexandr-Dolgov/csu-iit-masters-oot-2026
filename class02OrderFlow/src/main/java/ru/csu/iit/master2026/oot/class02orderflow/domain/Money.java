package ru.csu.iit.master2026.oot.class02orderflow.domain;

/**
 * Сумма в рублях, хранится в копейках.
 *
 * <p>Значимый объект: неизменяемый, равенство по значению, отрицательным быть не может.
 */
public record Money(long kopecks) implements Comparable<Money> {

    public static final Money ZERO = new Money(0);

    public Money {
        if (kopecks < 0) {
            throw new IllegalArgumentException("Сумма не может быть отрицательной: " + kopecks);
        }
    }

    public static Money rubles(long rubles) {
        return new Money(Math.multiplyExact(rubles, 100));
    }

    public Money plus(Money other) {
        return new Money(Math.addExact(kopecks, other.kopecks));
    }

    public Money times(int factor) {
        return new Money(Math.multiplyExact(kopecks, factor));
    }

    /** Разность; результат меньше нуля — исключение, как и при создании. */
    public Money minus(Money other) {
        return new Money(Math.subtractExact(kopecks, other.kopecks));
    }

    /** Процент от суммы, округление до копейки: половина копейки — вверх. */
    public Money percent(int percent) {
        return new Money((Math.multiplyExact(kopecks, percent) + 50) / 100);
    }

    public boolean isAtLeast(Money other) {
        return kopecks >= other.kopecks;
    }

    @Override
    public int compareTo(Money other) {
        return Long.compare(kopecks, other.kopecks);
    }

    @Override
    public String toString() {
        return "%d.%02d руб.".formatted(kopecks / 100, kopecks % 100);
    }
}
