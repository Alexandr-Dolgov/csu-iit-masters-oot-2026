package ru.csu.iit.master2026.oot.class01orderflow.legacy;

import ru.csu.iit.master2026.oot.class01orderflow.domain.Customer;
import ru.csu.iit.master2026.oot.class01orderflow.domain.DeliveryTariff;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Email;
import ru.csu.iit.master2026.oot.class01orderflow.domain.ExpressDelivery;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderId;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderItem;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Promo;

import java.util.List;
import java.util.Map;

/**
 * Стартовое состояние OrderFlow: вся логика службы доставки в одном классе.
 *
 * <p>Код намеренно не исправлен — это материал для демо и практики занятия 1.
 * Исправлены деньги ({@link Money} вместо {@code double}) и данные клиента и позиций
 * (значимые объекты вместо строк и параллельных массивов), доставка — {@link DeliveryTariff},
 * сумму заказа считает {@link Order}.
 * Строка заказа в хранилище — {@code Object[]}: 0 заказ, 1 статус.
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
        store.orders.put(id, new Object[]{order, "NEW"});

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
        Object[] row = store.orders.get(id);
        if (row == null) {
            System.out.println("Ошибка: заказ не найден " + id);
            return false;
        }
        if (!"NEW".equals(row[1])) {
            System.out.println("Ошибка: заказ " + id + " уже в статусе " + row[1]);
            return false;
        }
        Order order = (Order) row[0];
        Money total = order.total();
        if (!amount.equals(total)) {
            System.out.println("Ошибка: оплата " + amount + " не совпадает с суммой заказа " + total);
            return false;
        }
        row[1] = "PAID";
        sendEmail(order.customer().email(), "Заказ " + id + " оплачен");
        return true;
    }

    public boolean ship(OrderId id) {
        Object[] row = store.orders.get(id);
        if (row == null) {
            System.out.println("Ошибка: заказ не найден " + id);
            return false;
        }
        if (!"PAID".equals(row[1])) {
            System.out.println("Ошибка: нельзя отправить неоплаченный заказ " + id);
            return false;
        }
        row[1] = "SHIPPED";
        Order order = (Order) row[0];
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
        Object[] row = store.orders.get(id);
        if (row == null) {
            System.out.println("Ошибка: заказ не найден " + id);
            return false;
        }
        if ("SHIPPED".equals(row[1])) {
            System.out.println("Ошибка: отправленный заказ отменить нельзя " + id);
            return false;
        }
        Order order = (Order) row[0];
        if ("PAID".equals(row[1])) {
            System.out.println("Возврат " + order.total() + " по заказу " + id);
        }
        row[1] = "CANCELLED";
        sendEmail(order.customer().email(), "Заказ " + id + " отменён");
        return true;
    }

    public String report() {
        int count = 0;
        int express = 0;
        Money revenue = Money.ZERO;
        for (Map.Entry<OrderId, Object[]> e : store.orders.entrySet()) {
            Object[] row = e.getValue();
            Order order = (Order) row[0];
            count++;
            if (order.delivery() instanceof ExpressDelivery) {
                express++;
            }
            if ("PAID".equals(row[1]) || "SHIPPED".equals(row[1])) {
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
