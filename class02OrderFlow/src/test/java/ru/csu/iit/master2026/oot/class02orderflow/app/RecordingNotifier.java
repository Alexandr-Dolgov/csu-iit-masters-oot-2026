package ru.csu.iit.master2026.oot.class02orderflow.app;

import ru.csu.iit.master2026.oot.class02orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class02orderflow.domain.Order;

import java.util.ArrayList;
import java.util.List;

/** Фальшивый нотификатор: ничего не отправляет, только запоминает. */
class RecordingNotifier implements Notifier {

    final List<String> events = new ArrayList<>();

    @Override
    public void orderPlaced(Order order) {
        events.add("placed " + order.id());
    }

    @Override
    public void orderPaid(Order order) {
        events.add("paid " + order.id());
    }

    @Override
    public void orderShipped(Order order) {
        events.add("shipped " + order.id());
    }

    @Override
    public void orderCancelled(Order order, Money refund) {
        events.add("cancelled " + order.id() + " refund " + refund);
    }
}
