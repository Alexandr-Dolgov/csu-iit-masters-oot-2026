package ru.csu.iit.master2026.oot.class04orderflow.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.csu.iit.master2026.oot.class04orderflow.domain.CourierDelivery;
import ru.csu.iit.master2026.oot.class04orderflow.domain.Customer;
import ru.csu.iit.master2026.oot.class04orderflow.domain.CustomerId;
import ru.csu.iit.master2026.oot.class04orderflow.domain.Email;
import ru.csu.iit.master2026.oot.class04orderflow.domain.ExpressDelivery;
import ru.csu.iit.master2026.oot.class04orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class04orderflow.domain.OrderId;
import ru.csu.iit.master2026.oot.class04orderflow.infra.InMemoryOrderRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.csu.iit.master2026.oot.class04orderflow.domain.Money.rubles;

class SalesReportTest {

    private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
    private final Customer anna = new Customer(new CustomerId("c-1"), "Анна",
            new Email("anna@example.ru"), "+79000000001");

    @BeforeEach
    void twoOrders() {
        PlaceOrder placeOrder = new PlaceOrder(repository, new RecordingNotifier());
        Order paid = placeOrder.place(Order.builder()
                .customer(anna)
                .item("Ноутбук", 1, rubles(64990))
                .delivery(new ExpressDelivery()));
        paid.pay();
        placeOrder.place(Order.builder()
                .customer(anna)
                .item("Кружка", 1, rubles(350))
                .delivery(new CourierDelivery()));
    }

    @Test
    void один_скелет_текстовый_формат() {
        assertThat(new TextSalesReport(repository).render()).isEqualTo("""
                ORD-1  PAID      65490.00 руб.
                ORD-2  NEW       650.00 руб.
                Заказов: 2, экспресс: 1, выручка: 65490.00 руб., крупнейший: ORD-1""");
    }

    @Test
    void один_скелет_csv() {
        assertThat(new CsvSalesReport(repository).render()).isEqualTo("""
                id;status;total_kopecks
                ORD-1;PAID;6549000
                ORD-2;NEW;65000
                TOTAL;2;6549000
                """);
    }

    @Test
    void отчёту_хватает_истории_двойник_одной_лямбдой() {
        Order archived = Order.builder()
                .customer(anna)
                .item("Чайник", 1, rubles(2490))
                .delivery(new CourierDelivery())
                .build(new OrderId("ORD-7"));
        OrderHistory history = () -> List.of(archived);

        assertThat(new CsvSalesReport(history).render()).isEqualTo("""
                id;status;total_kopecks
                ORD-7;NEW;279000
                TOTAL;1;0
                """);
    }
}
