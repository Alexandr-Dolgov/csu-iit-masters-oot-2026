package ru.csu.iit.master2026.oot.class01orderflow.domain;

/** Курьер: 300 руб., бесплатно от 5000 руб. за товары, 3 дня. */
public record CourierDelivery() implements DeliveryTariff {

    private static final Money PRICE = Money.rubles(300);
    private static final Money FREE_FROM = Money.rubles(5000);

    @Override
    public Money cost(Money goods) {
        return goods.isAtLeast(FREE_FROM) ? Money.ZERO : PRICE;
    }

    @Override
    public int days() {
        return 3;
    }
}
