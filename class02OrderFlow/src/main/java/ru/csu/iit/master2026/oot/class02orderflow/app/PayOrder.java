package ru.csu.iit.master2026.oot.class02orderflow.app;

import ru.csu.iit.master2026.oot.class02orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class02orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class02orderflow.domain.OrderId;

/** Сценарий «оплатить заказ»: клиент платит ровно сумму заказа. */
public final class PayOrder {

    private final OrderRepository orders;
    private final Notifier notifier;

    public PayOrder(OrderRepository orders, Notifier notifier) {
        this.orders = orders;
        this.notifier = notifier;
    }

    public void pay(OrderId id, Money amount) {
        Order order = orders.find(id)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден " + id));
        if (!amount.equals(order.total())) {
            throw new IllegalArgumentException(
                    "Оплата " + amount + " не совпадает с суммой заказа " + order.total());
        }
        order.pay();
        orders.save(order);
        notifier.orderPaid(order);
    }
}
