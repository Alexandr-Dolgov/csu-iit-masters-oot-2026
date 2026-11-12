package ru.csu.iit.master2026.oot.class02orderflow.app;

import ru.csu.iit.master2026.oot.class02orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class02orderflow.domain.Order;

/** Как сообщить клиенту — SMS, почта, Telegram, — решает реализация. Роль — поддержка. */
public interface Notifier {

    void orderPlaced(Order order);

    void orderPaid(Order order);

    void orderShipped(Order order);

    /** @param refund сколько вернуть клиенту; {@link Money#ZERO}, если заказ не был оплачен */
    void orderCancelled(Order order, Money refund);
}
