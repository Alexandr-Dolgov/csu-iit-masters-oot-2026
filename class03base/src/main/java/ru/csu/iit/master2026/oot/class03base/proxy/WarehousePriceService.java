package ru.csu.iit.master2026.oot.class03base.proxy;

import java.util.Map;

/** Настоящий объект: каждый вызов — дорогой запрос на склад. Запросы считаем. */
public final class WarehousePriceService implements PriceService {

    private final Map<String, Integer> prices;
    private int requests = 0;

    public WarehousePriceService(Map<String, Integer> prices) {
        this.prices = Map.copyOf(prices);
    }

    @Override
    public int price(String sku) {
        requests++;
        Integer price = prices.get(sku);
        if (price == null) {
            throw new IllegalArgumentException("Нет такого товара " + sku);
        }
        return price;
    }

    public int requests() {
        return requests;
    }
}
