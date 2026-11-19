package ru.csu.iit.master2026.oot.class03base.decorator;

import java.util.Collection;
import java.util.Set;

/**
 * Паттерн «Декоратор»: тот же интерфейс Set, новое поведение — счёт добавлений.
 * Как устроено вложенное множество, не важно: декоратор зовёт только его публичные методы.
 */
public class InstrumentedSet<E> extends ForwardingSet<E> {

    private int addCount = 0;

    public InstrumentedSet(Set<E> s) {
        super(s);
    }

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
