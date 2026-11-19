package ru.csu.iit.master2026.oot.class03base.proxy;

import java.util.HashMap;
import java.util.Map;

/**
 * Паттерн «Прокси»: тот же интерфейс, что у настоящего объекта, и решение,
 * когда к нему обращаться. Клиент не отличает прокси от склада.
 */
public final class CachingPriceService implements PriceService {

    private final PriceService warehouse;
    private final Map<String, Integer> cache = new HashMap<>();

    public CachingPriceService(PriceService warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public int price(String sku) {
        return cache.computeIfAbsent(sku, warehouse::price);
    }
}
