package ru.csu.iit.master2026.oot.class04orderflow.infra;

import ru.csu.iit.master2026.oot.class04orderflow.app.CompositeNotifier;
import ru.csu.iit.master2026.oot.class04orderflow.app.Infrastructure;
import ru.csu.iit.master2026.oot.class04orderflow.app.Notifier;
import ru.csu.iit.master2026.oot.class04orderflow.app.OrderRepository;

/** Семейство для демо: заказы в памяти, уведомления в консоль. */
public final class ConsoleInfrastructure implements Infrastructure {

    private final OrderRepository orders = new InMemoryOrderRepository();
    /** Возвраты — бухгалтерии, остальное — клиенту. Для сценариев это один нотификатор. */
    private final Notifier notifier = new CompositeNotifier(new RefundNotifier(), new ConsoleNotifier());

    @Override
    public OrderRepository orders() {
        return orders;
    }

    @Override
    public Notifier notifier() {
        return notifier;
    }
}
