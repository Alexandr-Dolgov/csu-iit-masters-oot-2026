package ru.csu.iit.master2026.oot.class04base.observer;

import java.util.HashSet;
import java.util.Set;

/** Витрина: прячет товары, которых нет. */
public final class ShopWindow implements StockListener {

    private final Set<String> soldOut = new HashSet<>();

    @Override
    public void stockChanged(String sku, int before, int after) {
        if (after == 0) {
            soldOut.add(sku);
        } else {
            soldOut.remove(sku);
        }
    }

    public boolean shows(String sku) {
        return !soldOut.contains(sku);
    }
}
