package ru.csu.iit.master2026.oot.class03base.singleton;

/** Паттерн «Одиночка»: единственный экземпляр и глобальная точка доступа к нему. */
public final class Sequence {

    private static final Sequence INSTANCE = new Sequence();

    private int last = 0;

    private Sequence() {
    }

    public static Sequence getInstance() {
        return INSTANCE;
    }

    public int next() {
        last++;
        return last;
    }
}
