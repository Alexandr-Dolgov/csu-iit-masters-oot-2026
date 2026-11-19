package ru.csu.iit.master2026.oot.class03base.singleton;

/**
 * Одиночка-перечисление (Блох, Item 3): JVM гарантирует ровно один экземпляр,
 * даже при сериализации и рефлексии. Годится для объектов без состояния.
 */
public enum Rubles {

    FORMAT;

    public String format(int rubles) {
        return rubles + " руб.";
    }
}
