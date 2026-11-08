package ru.csu.iit.master2026.oot.class01orderflow.legacy;

import ru.csu.iit.master2026.oot.class01orderflow.domain.Money;

import java.util.Map;

/**
 * Стартовое состояние OrderFlow: вся логика службы доставки в одном классе.
 *
 * <p>Код намеренно не исправлен — это материал для демо и практики занятия 1.
 * Исправлены только деньги: {@link Money} вместо {@code double}.
 * Строка заказа в хранилище — {@code Object[]}:
 * 0 имя, 1 email, 2 телефон, 3 сумма, 4 доставка, 5 статус, 6 страховка.
 */
public class OrderService {

    private final InMemoryStore store = new InMemoryStore();
    private int counter = 0;

    public String place(String name, String email, String phone,
                        String[] items, int[] qty, Money[] prices,
                        String delivery, boolean insurance, String promo) {
        if (items == null || items.length == 0) {
            System.out.println("Ошибка: заказ без позиций");
            return null;
        }
        if (email == null || !email.contains("@")) {
            System.out.println("Ошибка: некорректный email " + email);
            return null;
        }

        Money total = Money.ZERO;
        for (int i = 0; i < items.length; i++) {
            if (qty[i] <= 0) {
                System.out.println("Ошибка: количество должно быть положительным: " + items[i]);
                return null;
            }
            total = total.plus(prices[i].times(qty[i]));
        }

        // промокоды действуют только на товары
        if ("SAVE10".equals(promo)) {
            total = total.minus(total.percent(10));
        } else if ("SAVE500".equals(promo) && total.isAtLeast(Money.rubles(3000))) {
            total = total.minus(Money.rubles(500));
        }

        Money deliveryCost;
        if ("express".equals(delivery)) {
            deliveryCost = Money.rubles(500);
        } else if ("courier".equals(delivery)) {
            deliveryCost = total.isAtLeast(Money.rubles(5000)) ? Money.ZERO : Money.rubles(300);
        } else {
            System.out.println("Ошибка: неизвестный тип доставки " + delivery);
            return null;
        }
        total = total.plus(deliveryCost);

        if (insurance) {
            total = total.plus(total.percent(3));
        }

        counter++;
        String id = "ORD-" + counter;
        store.orders.put(id, new Object[]{name, email, phone, total, delivery, "NEW", insurance});

        String text = name + ", заказ " + id + " принят. Сумма: " + total;
        if ("express".equals(delivery)) {
            text += " Экспресс-доставка: привезём завтра.";
        }
        if (total.compareTo(Money.rubles(10_000)) > 0) {
            sendSms(phone, text);
        } else {
            sendEmail(email, text);
        }
        return id;
    }

    public boolean pay(String id, Money amount) {
        Object[] row = store.orders.get(id);
        if (row == null) {
            System.out.println("Ошибка: заказ не найден " + id);
            return false;
        }
        if (!"NEW".equals(row[5])) {
            System.out.println("Ошибка: заказ " + id + " уже в статусе " + row[5]);
            return false;
        }
        Money total = (Money) row[3];
        if (!amount.equals(total)) {
            System.out.println("Ошибка: оплата " + amount + " не совпадает с суммой заказа " + total);
            return false;
        }
        row[5] = "PAID";
        sendEmail((String) row[1], "Заказ " + id + " оплачен");
        return true;
    }

    public boolean ship(String id) {
        Object[] row = store.orders.get(id);
        if (row == null) {
            System.out.println("Ошибка: заказ не найден " + id);
            return false;
        }
        if (!"PAID".equals(row[5])) {
            System.out.println("Ошибка: нельзя отправить неоплаченный заказ " + id);
            return false;
        }
        row[5] = "SHIPPED";
        int days = deliveryDays((String) row[4]);
        String text = "Заказ " + id + " передан в доставку, срок " + days + " дн.";
        if (((Money) row[3]).compareTo(Money.rubles(10_000)) > 0) {
            sendSms((String) row[2], text);
        } else {
            sendEmail((String) row[1], text);
        }
        return true;
    }

    public boolean cancel(String id) {
        Object[] row = store.orders.get(id);
        if (row == null) {
            System.out.println("Ошибка: заказ не найден " + id);
            return false;
        }
        if ("SHIPPED".equals(row[5])) {
            System.out.println("Ошибка: отправленный заказ отменить нельзя " + id);
            return false;
        }
        if ("PAID".equals(row[5])) {
            System.out.println("Возврат " + row[3] + " по заказу " + id);
        }
        row[5] = "CANCELLED";
        sendEmail((String) row[1], "Заказ " + id + " отменён");
        return true;
    }

    private int deliveryDays(String delivery) {
        if ("express".equals(delivery)) {
            return 1;
        }
        return 3;
    }

    public String report() {
        int count = 0;
        int express = 0;
        Money revenue = Money.ZERO;
        for (Map.Entry<String, Object[]> e : store.orders.entrySet()) {
            Object[] row = e.getValue();
            count++;
            if ("express".equals(row[4])) {
                express++;
            }
            if ("PAID".equals(row[5]) || "SHIPPED".equals(row[5])) {
                revenue = revenue.plus((Money) row[3]);
            }
        }
        return "Заказов: " + count + ", экспресс: " + express + ", выручка: " + revenue;
    }

    private void sendSms(String phone, String text) {
        System.out.println("[SMS " + phone + "] " + text);
    }

    private void sendEmail(String email, String text) {
        System.out.println("[EMAIL " + email + "] " + text);
    }
}
