package ru.csu.iit.master2026.oot.class03orderflow.app;

import ru.csu.iit.master2026.oot.class03orderflow.infra.InMemoryOrderRepository;

/** Семейство для тестов: заказы в памяти, уведомления запоминаются. Поля открыты для проверок. */
final class TestInfrastructure implements Infrastructure {

    final InMemoryOrderRepository orders = new InMemoryOrderRepository();
    final RecordingNotifier notifier = new RecordingNotifier();

    @Override
    public OrderRepository orders() {
        return orders;
    }

    @Override
    public Notifier notifier() {
        return notifier;
    }
}
