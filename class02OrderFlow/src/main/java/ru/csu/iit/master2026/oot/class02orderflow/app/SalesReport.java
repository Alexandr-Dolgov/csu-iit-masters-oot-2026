package ru.csu.iit.master2026.oot.class02orderflow.app;

import ru.csu.iit.master2026.oot.class02orderflow.domain.ExpressDelivery;
import ru.csu.iit.master2026.oot.class02orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class02orderflow.domain.Order;

import java.util.Comparator;

/** Отчёт для бухгалтерии: сколько заказов, сколько экспресс, выручка по оплаченным, крупнейший заказ. */
public final class SalesReport {

    private final OrderRepository orders;

    public SalesReport(OrderRepository orders) {
        this.orders = orders;
    }

    public String summary() {
        int count = 0;
        int express = 0;
        Money revenue = Money.ZERO;
        for (Order order : orders.findAll()) {
            count++;
            if (order.delivery() instanceof ExpressDelivery) {
                express++;
            }
            if (order.status().paid()) {
                revenue = revenue.plus(order.total());
            }
        }
        return "Заказов: " + count + ", экспресс: " + express + ", выручка: " + revenue
                + ", крупнейший: " + largest();
    }

    /** Как сравнивать — стратегия, которую передаём в max. */
    private String largest() {
        return orders.findAll().stream()
                .max(Comparator.comparing(Order::total))
                .map(order -> order.id().toString())
                .orElse("—");
    }
}
