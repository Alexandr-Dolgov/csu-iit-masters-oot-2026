package ru.csu.iit.master2026.oot.class01orderflow.app;

import ru.csu.iit.master2026.oot.class01orderflow.domain.CourierDelivery;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Customer;
import ru.csu.iit.master2026.oot.class01orderflow.domain.CustomerId;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Email;
import ru.csu.iit.master2026.oot.class01orderflow.domain.ExpressDelivery;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderItem;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Promo;
import ru.csu.iit.master2026.oot.class01orderflow.infra.ConsoleNotifier;
import ru.csu.iit.master2026.oot.class01orderflow.infra.InMemoryOrderRepository;

import java.util.List;

import static ru.csu.iit.master2026.oot.class01orderflow.domain.Money.rubles;

/** Сценарий legacy.Main после рефакторинга. Здесь, и только здесь, собирается приложение. */
public class Main {

    public static void main(String[] args) {
        OrderRepository orders = new InMemoryOrderRepository();
        Notifier notifier = new ConsoleNotifier();
        PlaceOrder placeOrder = new PlaceOrder(orders, notifier);
        PayOrder payOrder = new PayOrder(orders, notifier);
        ShipOrder shipOrder = new ShipOrder(orders, notifier);
        SalesReport report = new SalesReport(orders);

        Customer anna = new Customer(new CustomerId("c-1"), "Анна",
                new Email("anna@example.ru"), "+79000000001");
        Customer boris = new Customer(new CustomerId("c-2"), "Борис",
                new Email("boris@example.ru"), "+79000000002");
        Customer vera = new Customer(new CustomerId("c-3"), "Вера",
                new Email("vera@example.ru"), "+79000000003");
        List<OrderItem> kettleAndMugs = List.of(
                new OrderItem("Чайник", 1, rubles(2490)),
                new OrderItem("Кружка", 2, rubles(350)));

        Order first = placeOrder.place(anna, kettleAndMugs, new CourierDelivery());
        payOrder.pay(first.id(), rubles(3490));
        shipOrder.ship(first.id());

        // Тот же заказ со страховкой: 3490 + 3% = 3594.70 — клиент платит ровно столько
        Order second = placeOrder.place(boris, kettleAndMugs, new CourierDelivery(), Promo.NONE, true);
        payOrder.pay(second.id(), new Money(359_470));

        Order third = placeOrder.place(vera, List.of(new OrderItem("Ноутбук", 1, rubles(64990))),
                new ExpressDelivery(), Promo.SAVE10, false);
        payOrder.pay(third.id(), rubles(58991));
        shipOrder.ship(third.id());

        System.out.println(report.summary());
    }
}
