package ru.csu.iit.master2026.oot.class02orderflow.app;

import ru.csu.iit.master2026.oot.class02orderflow.domain.Order;

/** Отчёт для чтения глазами. */
public final class TextSalesReport extends SalesReport {

    public TextSalesReport(OrderRepository orders) {
        super(orders);
    }

    @Override
    protected void row(StringBuilder out, Order order) {
        out.append("%-6s %-9s %s\n".formatted(order.id(), order.status(), order.total()));
    }

    @Override
    protected void footer(StringBuilder out, Totals totals) {
        out.append("Заказов: ").append(totals.count())
                .append(", экспресс: ").append(totals.express())
                .append(", выручка: ").append(totals.revenue())
                .append(", крупнейший: ").append(totals.largest().map(Object::toString).orElse("—"));
    }
}
