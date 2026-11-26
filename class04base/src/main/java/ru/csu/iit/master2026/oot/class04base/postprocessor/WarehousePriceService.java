package ru.csu.iit.master2026.oot.class04base.postprocessor;

import java.util.Map;

/** Настоящий объект. О журнале не знает — только помечен {@link Logged}. */
@Logged
public final class WarehousePriceService implements PriceService {

    private final Map<String, Integer> prices;

    public WarehousePriceService(Map<String, Integer> prices) {
        this.prices = Map.copyOf(prices);
    }

    @Override
    public int price(String sku) {
        Integer price = prices.get(sku);
        if (price == null) {
            throw new IllegalArgumentException("Нет такого товара " + sku);
        }
        return price;
    }
}
