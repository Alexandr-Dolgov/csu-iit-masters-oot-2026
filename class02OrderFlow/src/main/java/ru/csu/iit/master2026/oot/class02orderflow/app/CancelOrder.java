package ru.csu.iit.master2026.oot.class02orderflow.app;

import ru.csu.iit.master2026.oot.class02orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class02orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class02orderflow.domain.OrderId;
import ru.csu.iit.master2026.oot.class02orderflow.domain.OrderStatus;

/** Сценарий «отменить заказ»: оплаченный заказ возвращает деньги. */
public final class CancelOrder {

    private final OrderRepository orders;
    private final Notifier notifier;

    public CancelOrder(OrderRepository orders, Notifier notifier) {
        this.orders = orders;
        this.notifier = notifier;
    }

    public void cancel(OrderId id) {
        Order order = orders.find(id)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден " + id));
        Money refund = order.status() == OrderStatus.PAID ? order.total() : Money.ZERO;
        order.cancel();
        orders.save(order);
        notifier.orderCancelled(order, refund);
    }
}
