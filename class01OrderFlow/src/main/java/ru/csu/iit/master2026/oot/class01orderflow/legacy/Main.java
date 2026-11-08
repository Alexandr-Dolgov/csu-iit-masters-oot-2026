package ru.csu.iit.master2026.oot.class01orderflow.legacy;

import ru.csu.iit.master2026.oot.class01orderflow.domain.CourierDelivery;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Customer;
import ru.csu.iit.master2026.oot.class01orderflow.domain.CustomerId;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Email;
import ru.csu.iit.master2026.oot.class01orderflow.domain.ExpressDelivery;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Money;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderId;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderItem;
import ru.csu.iit.master2026.oot.class01orderflow.domain.Promo;

import java.util.List;

import static ru.csu.iit.master2026.oot.class01orderflow.domain.Money.rubles;

/** Консольный сценарий стартового проекта. Запуск: Run 'Main.main()'. */
public class Main {

    public static void main(String[] args) {
        OrderService service = new OrderService();

        Customer anna = new Customer(new CustomerId("c-1"), "Анна",
                new Email("anna@example.ru"), "+79000000001");
        Customer boris = new Customer(new CustomerId("c-2"), "Борис",
                new Email("boris@example.ru"), "+79000000002");
        Customer vera = new Customer(new CustomerId("c-3"), "Вера",
                new Email("vera@example.ru"), "+79000000003");
        List<OrderItem> kettleAndMugs = List.of(
                new OrderItem("Чайник", 1, rubles(2490)),
                new OrderItem("Кружка", 2, rubles(350)));

        OrderId first = service.place(anna, kettleAndMugs, new CourierDelivery(), false, Promo.NONE);
        service.pay(first, rubles(3490));
        service.ship(first);

        // Тот же заказ со страховкой: 3490 + 3% = 3594.70 — клиент платит ровно столько
        OrderId second = service.place(boris, kettleAndMugs, new CourierDelivery(), true, Promo.NONE);
        service.pay(second, new Money(359_470));

        OrderId third = service.place(vera, List.of(new OrderItem("Ноутбук", 1, rubles(64990))),
                new ExpressDelivery(), false, Promo.SAVE10);
        service.pay(third, rubles(58991));
        service.ship(third);

        System.out.println(service.report());
    }
}
