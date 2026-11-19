package ru.csu.iit.master2026.oot.class03base.singleton;

/**
 * Обычный класс. Сколько счётчиков в приложении — один на всё или свой на каждый тест, —
 * решает тот, кто собирает приложение, а не сам класс.
 */
public final class Sequence {

    private int last = 0;

    public int next() {
        last++;
        return last;
    }
}
