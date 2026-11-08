package ru.csu.iit.master2026.oot.class01orderflow.app;

import ru.csu.iit.master2026.oot.class01orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderId;

/** Сценарий «передать заказ в доставку». */
public final class ShipOrder {

    private final OrderRepository orders;
    private final Notifier notifier;

    public ShipOrder(OrderRepository orders, Notifier notifier) {
        this.orders = orders;
        this.notifier = notifier;
    }

    public void ship(OrderId id) {
        Order order = orders.find(id)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден " + id));
        order.ship();
        orders.save(order);
        notifier.orderShipped(order);
    }
}
