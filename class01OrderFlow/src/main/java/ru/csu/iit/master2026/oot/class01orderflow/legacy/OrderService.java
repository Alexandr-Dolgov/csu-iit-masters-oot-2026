package ru.csu.iit.master2026.oot.class01orderflow.legacy;

import ru.csu.iit.master2026.oot.class01orderflow.domain.Customer;
import ru.csu.iit.master2026.oot.class01orderflow.domain.DeliveryTariff;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Email;
import ru.csu.iit.master2026.oot.class01orderflow.domain.ExpressDelivery;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderId;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderItem;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderStatus;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Promo;

import java.util.List;

/**
 * Стартовое состояние OrderFlow: вся логика службы доставки в одном классе.
 *
 * <p>Код намеренно не исправлен — это материал для демо и практики занятия 1.
 * Исправлены деньги ({@link Money} вместо {@code double}) и данные клиента и позиций
 * (значимые объекты вместо строк и параллельных массивов), доставка — {@link DeliveryTariff},
 * сумму и статус заказа держит {@link Order}.
 */
public class OrderService {

    private final InMemoryStore store = new InMemoryStore();
    private int counter = 0;

    public OrderId place(Customer customer, List<OrderItem> items,
                         DeliveryTariff delivery, boolean insurance, Promo promo) {
        Order order;
        try {
            order = new Order(new OrderId("ORD-" + (counter + 1)), customer, items, delivery,
                    promo, insurance);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return null;
        }
        counter++;
        OrderId id = order.id();
        Money total = order.total();
        store.orders.put(id, order);

        String text = customer.name() + ", заказ " + id + " принят. Сумма: " + total;
        if (delivery instanceof ExpressDelivery) {
            text += " Экспресс-доставка: привезём завтра.";
        }
        if (total.compareTo(Money.rubles(10_000)) > 0) {
            sendSms(customer.phone(), text);
        } else {
            sendEmail(customer.email(), text);
        }
        return id;
    }

    public boolean pay(OrderId id, Money amount) {
        Order order = store.orders.get(id);
        if (order == null) {
            System.out.println("Ошибка: заказ не найден " + id);
            return false;
        }
        Money total = order.total();
        if (!amount.equals(total)) {
            System.out.println("Ошибка: оплата " + amount + " не совпадает с суммой заказа " + total);
            return false;
        }
        try {
            order.pay();
        } catch (IllegalStateException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return false;
        }
        sendEmail(order.customer().email(), "Заказ " + id + " оплачен");
        return true;
    }

    public boolean ship(OrderId id) {
        Order order = store.orders.get(id);
        if (order == null) {
            System.out.println("Ошибка: заказ не найден " + id);
            return false;
        }
        try {
            order.ship();
        } catch (IllegalStateException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return false;
        }
        String text = "Заказ " + id + " передан в доставку, срок " + order.delivery().days() + " дн.";
        Customer customer = order.customer();
        if (order.total().compareTo(Money.rubles(10_000)) > 0) {
            sendSms(customer.phone(), text);
        } else {
            sendEmail(customer.email(), text);
        }
        return true;
    }

    public boolean cancel(OrderId id) {
        Order order = store.orders.get(id);
        if (order == null) {
            System.out.println("Ошибка: заказ не найден " + id);
            return false;
        }
        boolean wasPaid = order.status() == OrderStatus.PAID;
        try {
            order.cancel();
        } catch (IllegalStateException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return false;
        }
        if (wasPaid) {
            System.out.println("Возврат " + order.total() + " по заказу " + id);
        }
        sendEmail(order.customer().email(), "Заказ " + id + " отменён");
        return true;
    }

    public String report() {
        int count = 0;
        int express = 0;
        Money revenue = Money.ZERO;
        for (Order order : store.orders.values()) {
            count++;
            if (order.delivery() instanceof ExpressDelivery) {
                express++;
            }
            if (order.status() == OrderStatus.PAID || order.status() == OrderStatus.SHIPPED) {
                revenue = revenue.plus(order.total());
            }
        }
        return "Заказов: " + count + ", экспресс: " + express + ", выручка: " + revenue;
    }

    private void sendSms(String phone, String text) {
        System.out.println("[SMS " + phone + "] " + text);
    }

    private void sendEmail(Email email, String text) {
        System.out.println("[EMAIL " + email + "] " + text);
    }
}
