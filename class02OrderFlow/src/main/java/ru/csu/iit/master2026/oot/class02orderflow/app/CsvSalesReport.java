package ru.csu.iit.master2026.oot.class02orderflow.app;

import ru.csu.iit.master2026.oot.class02orderflow.domain.Order;

/** Отчёт для таблицы бухгалтерии: суммы в копейках, без «руб.». */
public final class CsvSalesReport extends SalesReport {

    public CsvSalesReport(OrderRepository orders) {
        super(orders);
    }

    @Override
    protected void header(StringBuilder out) {
        out.append("id;status;total_kopecks\n");
    }

    @Override
    protected void row(StringBuilder out, Order order) {
        out.append(order.id()).append(';')
                .append(order.status()).append(';')
                .append(order.total().kopecks()).append('\n');
    }

    @Override
    protected void footer(StringBuilder out, Totals totals) {
        out.append("TOTAL;").append(totals.count()).append(';')
                .append(totals.revenue().kopecks()).append('\n');
    }
}
