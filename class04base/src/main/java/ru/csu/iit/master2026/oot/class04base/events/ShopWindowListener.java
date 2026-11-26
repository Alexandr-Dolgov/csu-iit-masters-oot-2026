package ru.csu.iit.master2026.oot.class04base.events;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

/** Подписка — аннотация: какое событие нужно, видно по типу параметра. */
public final class ShopWindowListener {

    private final EventLog log;

    public ShopWindowListener(EventLog log) {
        this.log = log;
    }

    @EventListener
    @Order(1)
    public void on(StockChanged event) {
        if (event.after() == 0) {
            log.add("витрина: скрыть " + event.sku());
        }
    }
}
