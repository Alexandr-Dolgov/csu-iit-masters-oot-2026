package ru.csu.iit.master2026.oot.class01orderflow.app;

import org.junit.jupiter.api.Test;
import ru.csu.iit.master2026.oot.class01orderflow.domain.CourierDelivery;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Customer;
import ru.csu.iit.master2026.oot.class01orderflow.domain.CustomerId;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Email;
import ru.csu.iit.master2026.oot.class01orderflow.domain.ExpressDelivery;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderItem;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderStatus;
import ru.csu.iit.master2026.oot.class01orderflow.infra.InMemoryOrderRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.csu.iit.master2026.oot.class01orderflow.domain.Money.rubles;

class OrderScenariosTest {

    private final Customer anna = new Customer(new CustomerId("c-1"), "Анна",
            new Email("anna@example.ru"), "+79000000001");
    private final List<OrderItem> laptop = List.of(new OrderItem("Ноутбук", 1, rubles(64990)));

    private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
    private final RecordingNotifier notifier = new RecordingNotifier();
    private final PlaceOrder placeOrder = new PlaceOrder(repository, notifier);
    private final PayOrder payOrder = new PayOrder(repository, notifier);
    private final CancelOrder cancelOrder = new CancelOrder(repository, notifier);

    @Test
    void заказ_сохранён_и_клиент_уведомлён() {
        Order order = placeOrder.place(anna, laptop, new ExpressDelivery());

        assertThat(repository.find(order.id())).contains(order);
        assertThat(notifier.events).containsExactly("placed ORD-1");
    }

    @Test
    void оплата_не_по_чеку_отклоняется() {
        Order order = placeOrder.place(anna, laptop, new CourierDelivery());

        assertThatThrownBy(() -> payOrder.pay(order.id(), rubles(1)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(order.status()).isEqualTo(OrderStatus.NEW);
    }

    @Test
    void отмена_оплаченного_возвращает_деньги() {
        Order order = placeOrder.place(anna, laptop, new CourierDelivery());
        payOrder.pay(order.id(), rubles(64990));

        cancelOrder.cancel(order.id());

        assertThat(notifier.events).containsExactly(
                "placed ORD-1", "paid ORD-1", "cancelled ORD-1 refund 64990.00 руб.");
    }
}
