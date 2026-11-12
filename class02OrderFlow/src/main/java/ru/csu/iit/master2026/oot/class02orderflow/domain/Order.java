package ru.csu.iit.master2026.oot.class02orderflow.domain;

import java.util.List;
import java.util.Objects;

/**
 * Заказ — сущность: равенство по идентификатору, состояние меняется.
 *
 * <p>Инварианты:
 * <ul>
 *   <li>в заказе есть хотя бы одна позиция;</li>
 *   <li>статус движется только вперёд: NEW → PAID → SHIPPED;</li>
 *   <li>отправленный заказ отменить нельзя.</li>
 * </ul>
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
    private OrderStatus status = OrderStatus.NEW;

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

    public void pay() {
        requireStatus(OrderStatus.NEW);
        status = OrderStatus.PAID;
    }

    public void ship() {
        requireStatus(OrderStatus.PAID);
        status = OrderStatus.SHIPPED;
    }

    public void cancel() {
        if (status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Отправленный заказ отменить нельзя: " + id);
        }
        status = OrderStatus.CANCELLED;
    }

    private void requireStatus(OrderStatus expected) {
        if (status != expected) {
            throw new IllegalStateException(
                    "Заказ " + id + " в статусе " + status + ", ожидался " + expected);
        }
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

    public OrderStatus status() {
        return status;
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
        return "Order[" + id + ", " + status + ", " + total() + "]";
    }
}
