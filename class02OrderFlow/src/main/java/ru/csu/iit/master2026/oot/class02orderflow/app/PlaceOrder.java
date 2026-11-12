package ru.csu.iit.master2026.oot.class02orderflow.app;

import ru.csu.iit.master2026.oot.class02orderflow.domain.Order;

/** Сценарий «оформить заказ». Зависимости приходят снаружи, через конструктор. */
public final class PlaceOrder {

    private final OrderRepository orders;
    private final Notifier notifier;

    public PlaceOrder(OrderRepository orders, Notifier notifier) {
        this.orders = orders;
        this.notifier = notifier;
    }

    /** Одна сигнатура вместо перегрузок: что указать в заказе, решает черновик. */
    public Order place(Order.Builder draft) {
        Order order = draft.build(orders.nextId());
        orders.save(order);
        notifier.orderPlaced(order);
        return order;
    }
}
