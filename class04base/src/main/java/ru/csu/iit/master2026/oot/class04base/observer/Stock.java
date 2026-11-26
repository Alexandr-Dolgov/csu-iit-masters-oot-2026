package ru.csu.iit.master2026.oot.class04base.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Паттерн «Наблюдатель»: склад-издатель знает только интерфейс {@link StockListener}.
 * Кто подписан — витрина, закупки, аналитика, — склад не знает и от них не зависит.
 */
public final class Stock {

    private final Map<String, Integer> quantities = new HashMap<>();
    private final List<StockListener> listeners = new ArrayList<>();

    public void subscribe(StockListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(StockListener listener) {
        listeners.remove(listener);
    }

    public void set(String sku, int quantity) {
        int before = quantities.getOrDefault(sku, 0);
        quantities.put(sku, quantity);
        for (StockListener listener : listeners) {
            listener.stockChanged(sku, before, quantity);
        }
    }
}
