package ru.csu.iit.master2026.oot.class04base.events;

/** Событие — обычная запись. Наследовать {@code ApplicationEvent} не нужно. */
public record StockChanged(String sku, int before, int after) {
}
