package ru.csu.iit.master2026.oot.class04base.observer;

import java.util.ArrayList;
import java.util.List;

/** Закупки: просят пополнить, когда остаток опустился до порога. */
public final class RestockAlert implements StockListener {

    private final int threshold;
    private final List<String> requests = new ArrayList<>();

    public RestockAlert(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void stockChanged(String sku, int before, int after) {
        if (before > threshold && after <= threshold) {
            requests.add("пополнить " + sku);
        }
    }

    public List<String> requests() {
        return List.copyOf(requests);
    }
}
