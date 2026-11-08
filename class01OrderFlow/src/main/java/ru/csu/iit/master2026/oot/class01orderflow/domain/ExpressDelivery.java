package ru.csu.iit.master2026.oot.class01orderflow.domain;

/** Экспресс: 500 руб., на следующий день. */
public record ExpressDelivery() implements DeliveryTariff {

    private static final Money PRICE = Money.rubles(500);

    @Override
    public Money cost(Money goods) {
        return PRICE;
    }

    @Override
    public int days() {
        return 1;
    }
}
