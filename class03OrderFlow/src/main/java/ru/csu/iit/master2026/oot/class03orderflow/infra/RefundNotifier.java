package ru.csu.iit.master2026.oot.class03orderflow.infra;

import ru.csu.iit.master2026.oot.class03orderflow.app.Notifier;
import ru.csu.iit.master2026.oot.class03orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class03orderflow.domain.Order;

/** Бухгалтерия: ей важны только возвраты. Роль — финансы, не поддержка. */
public final class RefundNotifier implements Notifier {

    @Override
    public void orderPlaced(Order order) {
    }

    @Override
    public void orderPaid(Order order) {
    }

    @Override
    public void orderShipped(Order order) {
    }

    @Override
    public void orderCancelled(Order order, Money refund) {
        if (refund.compareTo(Money.ZERO) > 0) {
            System.out.println("Возврат " + refund + " по заказу " + order.id());
        }
    }
}
