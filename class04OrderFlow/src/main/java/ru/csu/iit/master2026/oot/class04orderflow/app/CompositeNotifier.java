package ru.csu.iit.master2026.oot.class04orderflow.app;

import ru.csu.iit.master2026.oot.class04orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class04orderflow.domain.Order;

import java.util.List;

/**
 * Паттерн «Компоновщик»: несколько нотификаторов под видом одного.
 * Сценарии по-прежнему знают один Notifier — сколько их на самом деле, решает сборка.
 */
public final class CompositeNotifier implements Notifier {

    private final List<Notifier> notifiers;

    public CompositeNotifier(Notifier... notifiers) {
        this.notifiers = List.of(notifiers);
    }

    @Override
    public void orderPlaced(Order order) {
        notifiers.forEach(n -> n.orderPlaced(order));
    }

    @Override
    public void orderPaid(Order order) {
        notifiers.forEach(n -> n.orderPaid(order));
    }

    @Override
    public void orderShipped(Order order) {
        notifiers.forEach(n -> n.orderShipped(order));
    }

    @Override
    public void orderCancelled(Order order, Money refund) {
        notifiers.forEach(n -> n.orderCancelled(order, refund));
    }
}
