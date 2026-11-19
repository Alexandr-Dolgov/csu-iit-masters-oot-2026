package ru.csu.iit.master2026.oot.class03base.composite;

/** Лист: отдельный товар. */
public record Item(String name, int price) implements Product {
}
