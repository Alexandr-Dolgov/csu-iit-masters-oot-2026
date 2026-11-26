package ru.csu.iit.master2026.oot.class03base.decorator;

import java.util.Collection;
import java.util.HashSet;

/** Множество, которое считает попытки добавить элемент. Наследует HashSet — и зависит от его устройства. */
public class InstrumentedHashSet<E> extends HashSet<E> {

    private int addCount = 0;

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int addCount() {
        return addCount;
    }
}
