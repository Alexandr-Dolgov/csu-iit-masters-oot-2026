package ru.csu.iit.master2026.oot.class02orderflow.infra;

import ru.csu.iit.master2026.oot.class02orderflow.app.Notifier;
import ru.csu.iit.master2026.oot.class02orderflow.domain.ExpressDelivery;
import ru.csu.iit.master2026.oot.class02orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class02orderflow.domain.Order;

/** Правила поддержки из legacy: тексты сообщений; крупные заказы — по SMS, остальные — письмом. */
public final class ConsoleNotifier implements Notifier {

    private static final Money SMS_FROM = Money.rubles(10_000);

    @Override
    public void orderPlaced(Order order) {
        String text = order.customer().name() + ", заказ " + order.id()
                + " принят. Сумма: " + order.total();
        if (order.delivery() instanceof ExpressDelivery) {
            text += " Экспресс-доставка: привезём завтра.";
        }
        smsOrEmail(order, text);
    }

    @Override
    public void orderPaid(Order order) {
        email(order, "Заказ " + order.id() + " оплачен");
    }

    @Override
    public void orderShipped(Order order) {
        smsOrEmail(order, "Заказ " + order.id() + " передан в доставку, срок "
                + order.delivery().days() + " дн.");
    }

    @Override
    public void orderCancelled(Order order, Money refund) {
        if (refund.compareTo(Money.ZERO) > 0) {
            System.out.println("Возврат " + refund + " по заказу " + order.id());
        }
        email(order, "Заказ " + order.id() + " отменён");
    }

    private void smsOrEmail(Order order, String text) {
        if (order.total().compareTo(SMS_FROM) > 0) {
            System.out.println("[SMS " + order.customer().phone() + "] " + text);
        } else {
            email(order, text);
        }
    }

    private void email(Order order, String text) {
        System.out.println("[EMAIL " + order.customer().email() + "] " + text);
    }
}
