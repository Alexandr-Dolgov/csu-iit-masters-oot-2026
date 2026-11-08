package ru.csu.iit.master2026.oot.class01orderflow.legacy;

import ru.csu.iit.master2026.oot.class01orderflow.domain.Customer;
import ru.csu.iit.master2026.oot.class01orderflow.domain.DeliveryTariff;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Email;
import ru.csu.iit.master2026.oot.class01orderflow.domain.ExpressDelivery;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderId;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderItem;

import java.util.List;
import java.util.Map;

/**
 * Стартовое состояние OrderFlow: вся логика службы доставки в одном классе.
 *
 * <p>Код намеренно не исправлен — это материал для демо и практики занятия 1.
 * Исправлены деньги ({@link Money} вместо {@code double}) и данные клиента и позиций
 * (значимые объекты вместо строк и параллельных массивов), доставка — {@link DeliveryTariff}.
 * Строка заказа в хранилище — {@code Object[]}:
 * 0 клиент, 1 сумма, 2 доставка, 3 статус, 4 страховка.
 */
public class OrderService {

    private final InMemoryStore store = new InMemoryStore();
    private int counter = 0;

    public OrderId place(Customer customer, List<OrderItem> items,
                         DeliveryTariff delivery, boolean insurance, String promo) {
        if (items.isEmpty()) {
            System.out.println("Ошибка: заказ без позиций");
            return null;
        }

        Money total = Money.ZERO;
        for (OrderItem item : items) {
            total = total.plus(item.subtotal());
        }

        // промокоды действуют только на товары
        if ("SAVE10".equals(promo)) {
            total = total.minus(total.percent(10));
        } else if ("SAVE500".equals(promo) && total.isAtLeast(Money.rubles(3000))) {
            total = total.minus(Money.rubles(500));
        }

        total = total.plus(delivery.cost(total));

        if (insurance) {
            total = total.plus(total.percent(3));
        }

        counter++;
        OrderId id = new OrderId("ORD-" + counter);
        store.orders.put(id, new Object[]{customer, total, delivery, "NEW", insurance});

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
        if (!"NEW".equals(row[3])) {
            System.out.println("Ошибка: заказ " + id + " уже в статусе " + row[3]);
            return false;
        }
        Money total = (Money) row[1];
        if (!amount.equals(total)) {
            System.out.println("Ошибка: оплата " + amount + " не совпадает с суммой заказа " + total);
            return false;
        }
        row[3] = "PAID";
        sendEmail(((Customer) row[0]).email(), "Заказ " + id + " оплачен");
        return true;
    }

    public boolean ship(OrderId id) {
        Object[] row = store.orders.get(id);
        if (row == null) {
            System.out.println("Ошибка: заказ не найден " + id);
            return false;
        }
        if (!"PAID".equals(row[3])) {
            System.out.println("Ошибка: нельзя отправить неоплаченный заказ " + id);
            return false;
        }
        row[3] = "SHIPPED";
        int days = ((DeliveryTariff) row[2]).days();
        String text = "Заказ " + id + " передан в доставку, срок " + days + " дн.";
        Customer customer = (Customer) row[0];
        if (((Money) row[1]).compareTo(Money.rubles(10_000)) > 0) {
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
        if ("SHIPPED".equals(row[3])) {
            System.out.println("Ошибка: отправленный заказ отменить нельзя " + id);
            return false;
        }
        if ("PAID".equals(row[3])) {
            System.out.println("Возврат " + row[1] + " по заказу " + id);
        }
        row[3] = "CANCELLED";
        sendEmail(((Customer) row[0]).email(), "Заказ " + id + " отменён");
        return true;
    }

    public String report() {
        int count = 0;
        int express = 0;
        Money revenue = Money.ZERO;
        for (Map.Entry<OrderId, Object[]> e : store.orders.entrySet()) {
            Object[] row = e.getValue();
            count++;
            if (row[2] instanceof ExpressDelivery) {
                express++;
            }
            if ("PAID".equals(row[3]) || "SHIPPED".equals(row[3])) {
                revenue = revenue.plus((Money) row[1]);
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
