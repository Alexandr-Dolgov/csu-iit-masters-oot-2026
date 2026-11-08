package ru.csu.iit.master2026.oot.class01orderflow.domain;

import java.util.List;
import java.util.Objects;

/**
 * Заказ — сущность: равенство по идентификатору.
 *
 * <p>Инвариант: в заказе есть хотя бы одна позиция.
 *
 * <p>Сумма: промокод на товары → доставка от суммы товаров со скидкой → страховка 3% от всего.
 */
public final class Order {

    private final OrderId id;
    private final Customer customer;
    private final List<OrderItem> items;
    private final DeliveryTariff delivery;
    private final Promo promo;
    private final boolean insured;

    public Order(OrderId id, Customer customer, List<OrderItem> items, DeliveryTariff delivery) {
        this(id, customer, items, delivery, Promo.NONE, false);
    }

    public Order(OrderId id, Customer customer, List<OrderItem> items, DeliveryTariff delivery,
                 Promo promo, boolean insured) {
        this.id = Objects.requireNonNull(id);
        this.customer = Objects.requireNonNull(customer);
        this.delivery = Objects.requireNonNull(delivery);
        this.promo = Objects.requireNonNull(promo);
        this.insured = insured;
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Заказ не может быть пустым");
        }
        this.items = List.copyOf(items);
    }

    public Money goods() {
        return items.stream()
                .map(OrderItem::subtotal)
                .reduce(Money.ZERO, Money::plus);
    }

    public Money total() {
        Money goods = promo.apply(goods());
        Money total = goods.plus(delivery.cost(goods));
        return insured ? total.plus(total.percent(3)) : total;
    }

    public OrderId id() {
        return id;
    }

    public Customer customer() {
        return customer;
    }

    public List<OrderItem> items() {
        return items;   // неизменяемая копия
    }

    public DeliveryTariff delivery() {
        return delivery;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Order other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Order[" + id + ", " + total() + "]";
    }
}
