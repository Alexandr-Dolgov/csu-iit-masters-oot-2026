package ru.csu.iit.master2026.oot.class02orderflow.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Заказ — сущность: равенство по идентификатору, состояние меняется.
 *
 * <p>Инварианты:
 * <ul>
 *   <li>в заказе есть хотя бы одна позиция;</li>
 *   <li>статус движется только по разрешённым переходам — их знает {@link OrderStatus}.</li>
 * </ul>
 *
 * <p>Сумма: промокод на товары → доставка от суммы товаров со скидкой → страховка 3% от всего.
 *
 * <p>Паттерн «Итератор»: позиции можно обойти, но не получить список — ни индексов, ни size, ни add.
 *
 * <p>Паттерн «Строитель»: заказ собирается по шагам через {@link #builder()}, а проверяется
 * один раз — в {@link Builder#build(OrderId)}. Конструктор закрыт: недостроенного заказа не бывает.
 */
public final class Order implements Iterable<OrderItem> {

    private final OrderId id;
    private final Customer customer;
    private final List<OrderItem> items;
    private final DeliveryTariff delivery;
    private final Promo promo;
    private final boolean insured;
    private OrderStatus status = OrderStatus.NEW;

    private Order(OrderId id, Builder builder) {
        this.id = Objects.requireNonNull(id);
        this.customer = Objects.requireNonNull(builder.customer, "Не указан клиент");
        this.delivery = Objects.requireNonNull(builder.delivery, "Не указана доставка");
        this.promo = builder.promo;
        this.insured = builder.insured;
        if (builder.items.isEmpty()) {
            throw new IllegalArgumentException("Заказ не может быть пустым");
        }
        this.items = List.copyOf(builder.items);
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Черновик заказа: копит параметры, ничего не проверяет до {@link #build(OrderId)}. */
    public static final class Builder {

        private Customer customer;
        private final List<OrderItem> items = new ArrayList<>();
        private DeliveryTariff delivery;
        private Promo promo = Promo.NONE;
        private boolean insured = false;

        private Builder() {
        }

        public Builder customer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder item(String sku, int quantity, Money price) {
            items.add(new OrderItem(sku, quantity, price));
            return this;
        }

        public Builder items(List<OrderItem> items) {
            this.items.addAll(items);
            return this;
        }

        public Builder delivery(DeliveryTariff delivery) {
            this.delivery = delivery;
            return this;
        }

        public Builder promo(Promo promo) {
            this.promo = Objects.requireNonNull(promo);
            return this;
        }

        /** Вместо {@code true} пятым аргументом — имя того, что включаем. */
        public Builder insured() {
            this.insured = true;
            return this;
        }

        /** Идентификатор выдаёт хранилище — поэтому он приходит последним. */
        public Order build(OrderId id) {
            return new Order(id, this);
        }
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
        status = status.pay();
    }

    public void ship() {
        status = status.ship();
    }

    public void cancel() {
        status = status.cancel();
    }

    public OrderId id() {
        return id;
    }

    public Customer customer() {
        return customer;
    }

    /** Обход позиций. Итератор неизменяемой копии: remove бросит исключение. */
    @Override
    public Iterator<OrderItem> iterator() {
        return items.iterator();
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
