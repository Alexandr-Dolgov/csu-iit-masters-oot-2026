package ru.csu.iit.master2026.oot.class01orderflow.app;

import ru.csu.iit.master2026.oot.class01orderflow.domain.Customer;
import ru.csu.iit.master2026.oot.class01orderflow.domain.DeliveryTariff;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderItem;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Promo;

import java.util.List;

/** Сценарий «оформить заказ». Зависимости приходят снаружи, через конструктор. */
public final class PlaceOrder {

    private final OrderRepository orders;
    private final Notifier notifier;

    public PlaceOrder(OrderRepository orders, Notifier notifier) {
        this.orders = orders;
        this.notifier = notifier;
    }

    public Order place(Customer customer, List<OrderItem> items, DeliveryTariff delivery) {
        return place(customer, items, delivery, Promo.NONE, false);
    }

    public Order place(Customer customer, List<OrderItem> items, DeliveryTariff delivery,
                       Promo promo, boolean insured) {
        Order order = new Order(orders.nextId(), customer, items, delivery, promo, insured);
        orders.save(order);
        notifier.orderPlaced(order);
        return order;
    }
}
