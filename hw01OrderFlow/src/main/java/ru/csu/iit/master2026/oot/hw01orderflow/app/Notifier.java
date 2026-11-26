package ru.csu.iit.master2026.oot.hw01orderflow.app;

import ru.csu.iit.master2026.oot.hw01orderflow.domain.Money;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.Order;

/** Как сообщить клиенту — SMS, почта, Telegram, — решает реализация. Роль — поддержка. */
public interface Notifier {

    void orderPlaced(Order order);

    void orderPaid(Order order);

    void orderShipped(Order order);

    /** @param refund сколько вернуть клиенту; {@link Money#ZERO}, если заказ не был оплачен */
    void orderCancelled(Order order, Money refund);
}
