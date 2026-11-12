package ru.csu.iit.master2026.oot.class02base.equality;

import java.util.concurrent.atomic.AtomicInteger;

/** Точка, которая считает, сколько её создали. Новых значимых полей нет — это всё ещё точка. */
public class CountedPoint extends Point {

    private static final AtomicInteger created = new AtomicInteger();

    public CountedPoint(int x, int y) {
        super(x, y);
        created.incrementAndGet();
    }

    public static int created() {
        return created.get();
    }
}
