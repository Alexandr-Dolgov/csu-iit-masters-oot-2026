package ru.csu.iit.master2026.oot.class02orderflow.domain;

/** Самовывоз: бесплатно, в день заказа. То самое новое требование. */
public record Pickup() implements DeliveryTariff {

    @Override
    public Money cost(Money goods) {
        return Money.ZERO;
    }

    @Override
    public int days() {
        return 0;
    }
}
