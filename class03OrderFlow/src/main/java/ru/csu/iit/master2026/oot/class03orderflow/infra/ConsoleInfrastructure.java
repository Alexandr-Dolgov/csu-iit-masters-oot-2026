package ru.csu.iit.master2026.oot.class03orderflow.infra;

import ru.csu.iit.master2026.oot.class03orderflow.app.Infrastructure;
import ru.csu.iit.master2026.oot.class03orderflow.app.Notifier;
import ru.csu.iit.master2026.oot.class03orderflow.app.OrderRepository;

/** Семейство для демо: заказы в памяти, уведомления в консоль. */
public final class ConsoleInfrastructure implements Infrastructure {

    private final OrderRepository orders = new InMemoryOrderRepository();
    private final Notifier notifier = new ConsoleNotifier();

    @Override
    public OrderRepository orders() {
        return orders;
    }

    @Override
    public Notifier notifier() {
        return notifier;
    }
}
