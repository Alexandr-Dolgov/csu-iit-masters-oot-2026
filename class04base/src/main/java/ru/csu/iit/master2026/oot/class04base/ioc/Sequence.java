package ru.csu.iit.master2026.oot.class04base.ioc;

/** Счётчик из занятия 3 — обычный класс. Сколько счётчиков, решает сборка. */
public final class Sequence {

    private int last = 0;

    public int next() {
        last++;
        return last;
    }
}
