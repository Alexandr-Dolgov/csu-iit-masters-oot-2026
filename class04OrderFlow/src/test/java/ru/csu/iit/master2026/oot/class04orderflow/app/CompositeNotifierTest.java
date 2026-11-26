package ru.csu.iit.master2026.oot.class04orderflow.app;

import org.junit.jupiter.api.Test;
import ru.csu.iit.master2026.oot.class04orderflow.domain.Customer;
import ru.csu.iit.master2026.oot.class04orderflow.domain.CustomerId;
import ru.csu.iit.master2026.oot.class04orderflow.domain.Email;
import ru.csu.iit.master2026.oot.class04orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class04orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class04orderflow.domain.OrderId;
import ru.csu.iit.master2026.oot.class04orderflow.domain.Pickup;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.csu.iit.master2026.oot.class04orderflow.domain.Money.rubles;

class CompositeNotifierTest {

    private final Order order = Order.builder()
            .customer(new Customer(new CustomerId("c-1"), "Анна",
                    new Email("anna@example.ru"), "+79000000001"))
            .item("Кружка", 1, rubles(350))
            .delivery(new Pickup())
            .build(new OrderId("ORD-1"));

    @Test
    void событие_получают_все_по_порядку() {
        RecordingNotifier support = new RecordingNotifier();
        RecordingNotifier accounting = new RecordingNotifier();
        Notifier all = new CompositeNotifier(support, accounting);

        all.orderPlaced(order);
        all.orderCancelled(order, Money.ZERO);

        assertThat(support.events).containsExactly("placed ORD-1", "cancelled ORD-1 refund 0.00 руб.");
        assertThat(accounting.events).isEqualTo(support.events);
    }

    @Test
    void компоновщик_вкладывается_в_компоновщик() {
        RecordingNotifier deep = new RecordingNotifier();
        Notifier tree = new CompositeNotifier(new CompositeNotifier(deep), new RecordingNotifier());

        tree.orderPaid(order);

        assertThat(deep.events).containsExactly("paid ORD-1");
    }

    @Test
    void пустой_компоновщик_ничего_не_делает() {
        new CompositeNotifier().orderShipped(order);
    }
}
