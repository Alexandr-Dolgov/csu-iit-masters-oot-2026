package ru.csu.iit.master2026.oot.class03orderflow.app;

import ru.csu.iit.master2026.oot.class03orderflow.domain.CourierDelivery;
import ru.csu.iit.master2026.oot.class03orderflow.domain.Customer;
import ru.csu.iit.master2026.oot.class03orderflow.domain.CustomerId;
import ru.csu.iit.master2026.oot.class03orderflow.domain.Email;
import ru.csu.iit.master2026.oot.class03orderflow.domain.ExpressDelivery;
import ru.csu.iit.master2026.oot.class03orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class03orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class03orderflow.domain.OrderItem;
import ru.csu.iit.master2026.oot.class03orderflow.domain.Pickup;
import ru.csu.iit.master2026.oot.class03orderflow.domain.Promo;
import ru.csu.iit.master2026.oot.class03orderflow.infra.ConsoleInfrastructure;

import java.util.List;

import static ru.csu.iit.master2026.oot.class03orderflow.domain.Money.rubles;

/** Сценарий legacy.Main после рефакторинга. Здесь, и только здесь, собирается приложение. */
public class Main {

    public static void main(String[] args) {
        // Единственное место, где названо конкретное семейство
        Infrastructure infra = new ConsoleInfrastructure();
        PlaceOrder placeOrder = new PlaceOrder(infra.orders(), infra.notifier());
        PayOrder payOrder = new PayOrder(infra.orders(), infra.notifier());
        ShipOrder shipOrder = new ShipOrder(infra.orders(), infra.notifier());
        SalesReport report = new TextSalesReport(infra.orders());
        SalesReport csv = new CsvSalesReport(infra.orders());

        Customer anna = new Customer(new CustomerId("c-1"), "Анна",
                new Email("anna@example.ru"), "+79000000001");
        Customer boris = new Customer(new CustomerId("c-2"), "Борис",
                new Email("boris@example.ru"), "+79000000002");
        Customer vera = new Customer(new CustomerId("c-3"), "Вера",
                new Email("vera@example.ru"), "+79000000003");
        List<OrderItem> kettleAndMugs = List.of(
                new OrderItem("Чайник", 1, rubles(2490)),
                new OrderItem("Кружка", 2, rubles(350)));

        Order first = placeOrder.place(Order.builder()
                .customer(anna)
                .items(kettleAndMugs)
                .delivery(new CourierDelivery()));
        payOrder.pay(first.id(), rubles(3490));
        shipOrder.ship(first.id());

        // Тот же заказ со страховкой: 3490 + 3% = 3594.70 — клиент платит ровно столько
        Order second = placeOrder.place(Order.builder()
                .customer(boris)
                .items(kettleAndMugs)
                .delivery(new CourierDelivery())
                .insured());
        payOrder.pay(second.id(), new Money(359_470));

        Order third = placeOrder.place(Order.builder()
                .customer(vera)
                .item("Ноутбук", 1, rubles(64990))
                .delivery(new ExpressDelivery())
                .promo(Promo.SAVE10));
        payOrder.pay(third.id(), rubles(58991));
        shipOrder.ship(third.id());

        // Новое требование логистики: самовывоз, бесплатно, в день заказа
        placeOrder.place(Order.builder()
                .customer(anna)
                .items(kettleAndMugs)
                .delivery(new Pickup()));

        System.out.println(report.render());
        System.out.println();
        System.out.print(csv.render());
    }
}
