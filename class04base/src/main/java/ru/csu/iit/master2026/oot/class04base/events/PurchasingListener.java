package ru.csu.iit.master2026.oot.class04base.events;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

/** Закупки: пополнить, когда остаток опустился до порога. */
public final class PurchasingListener {

    private static final int THRESHOLD = 5;

    private final EventLog log;

    public PurchasingListener(EventLog log) {
        this.log = log;
    }

    @EventListener
    @Order(2)
    public void on(StockChanged event) {
        if (event.before() > THRESHOLD && event.after() <= THRESHOLD) {
            log.add("закупки: пополнить " + event.sku() + " [" + Thread.currentThread().getName() + "]");
        }
    }
}
