package ru.csu.iit.master2026.oot.class02orderflow.app;

import ru.csu.iit.master2026.oot.class02orderflow.domain.ExpressDelivery;
import ru.csu.iit.master2026.oot.class02orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class02orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class02orderflow.domain.OrderId;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Отчёт для бухгалтерии: строка на заказ и итоги.
 *
 * <p>Паттерн «Шаблонный метод»: {@link #render()} — неизменный скелет (обойти заказы, посчитать
 * итоги), а формат строк и итогов — шаги, которые определяют подклассы.
 */
public abstract class SalesReport {

    /** Итоги отчёта: сколько заказов, сколько экспресс, выручка по оплаченным, крупнейший. */
    public record Totals(int count, int express, Money revenue, Optional<OrderId> largest) {
    }

    private final OrderRepository orders;

    protected SalesReport(OrderRepository orders) {
        this.orders = orders;
    }

    /** Шаблонный метод: final — подкласс меняет шаги, но не порядок. */
    public final String render() {
        StringBuilder out = new StringBuilder();
        header(out);
        List<Order> all = orders.findAll();
        int express = 0;
        Money revenue = Money.ZERO;
        for (Order order : all) {
            if (order.delivery() instanceof ExpressDelivery) {
                express++;
            }
            if (order.status().paid()) {
                revenue = revenue.plus(order.total());
            }
            row(out, order);
        }
        Optional<OrderId> largest = all.stream()
                .max(Comparator.comparing(Order::total))
                .map(Order::id);
        footer(out, new Totals(all.size(), express, revenue, largest));
        return out.toString();
    }

    /** Крючок (hook): по умолчанию заголовка нет. */
    protected void header(StringBuilder out) {
    }

    protected abstract void row(StringBuilder out, Order order);

    protected abstract void footer(StringBuilder out, Totals totals);
}
