package ru.csu.iit.master2026.oot.class04base.observer;

/** Наблюдатель: что делать, когда остаток изменился, решает он сам. */
public interface StockListener {

    void stockChanged(String sku, int before, int after);
}
