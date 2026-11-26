package ru.csu.iit.master2026.oot.class04base.events;

import org.springframework.context.ApplicationEventPublisher;

import java.util.HashMap;
import java.util.Map;

/** Склад из пакета observer, но список подписчиков ведёт контейнер. Склад знает только издателя. */
public final class SpringStock {

    private final Map<String, Integer> quantities = new HashMap<>();
    private final ApplicationEventPublisher events;

    public SpringStock(ApplicationEventPublisher events) {
        this.events = events;
    }

    public void set(String sku, int quantity) {
        int before = quantities.getOrDefault(sku, 0);
        quantities.put(sku, quantity);
        events.publishEvent(new StockChanged(sku, before, quantity));
    }
}
