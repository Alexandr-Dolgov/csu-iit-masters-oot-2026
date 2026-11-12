package ru.csu.iit.master2026.oot.class02orderflow.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.csu.iit.master2026.oot.class02orderflow.domain.Money.rubles;

class OrderTest {

    private final OrderId id = new OrderId("ORD-1");
    private final Customer customer = new Customer(new CustomerId("c-1"), "Анна",
            new Email("anna@example.ru"), "+79000000001");
    private final List<OrderItem> items = List.of(
            new OrderItem("Чайник", 1, rubles(2490)),
            new OrderItem("Кружка", 2, rubles(350)));

    @Test
    void пустой_заказ_создать_нельзя() {
        assertThatThrownBy(() -> Order.builder().customer(customer).items(List.of()).delivery(new CourierDelivery()).build(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("пустым");
    }

    @Test
    void без_доставки_не_собрать() {
        var draft = Order.builder().customer(customer).items(items);

        assertThatThrownBy(() -> draft.build(id))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Не указана доставка");
    }

    @Test
    void строитель_собирает_по_шагам() {
        var order = Order.builder()
                .customer(customer)
                .item("Чайник", 1, rubles(2490))
                .item("Кружка", 2, rubles(350))
                .delivery(new CourierDelivery())
                .insured()
                .build(id);

        assertThat(order.total()).isEqualTo(new Money(359_470));
    }

    @Test
    void сумма_складывается_из_товаров_и_доставки() {
        var order = Order.builder().customer(customer).items(items).delivery(new CourierDelivery()).build(id);

        assertThat(order.total()).isEqualTo(rubles(3490));
    }

    @Test
    void самовывоз_бесплатный() {
        var order = Order.builder().customer(customer).items(items).delivery(new Pickup()).build(id);

        assertThat(order.total()).isEqualTo(rubles(3190));
    }

    @Test
    void страховка_3_процента_от_всей_суммы() {
        var order = Order.builder().customer(customer).items(items).delivery(new CourierDelivery()).insured().build(id);

        assertThat(order.total()).isEqualTo(new Money(359_470));
    }

    @Test
    void промокод_действует_только_на_товары() {
        var laptop = List.of(new OrderItem("Ноутбук", 1, rubles(64990)));
        var order = Order.builder().customer(customer).items(laptop).delivery(new ExpressDelivery()).promo(Promo.SAVE10).build(id);

        assertThat(order.total()).isEqualTo(rubles(58491 + 500));
    }

    @Test
    void save500_не_действует_до_3000() {
        var mug = List.of(new OrderItem("Кружка", 1, rubles(350)));
        var order = Order.builder().customer(customer).items(mug).delivery(new CourierDelivery()).promo(Promo.SAVE500).build(id);

        assertThat(order.total()).isEqualTo(rubles(350 + 300));
    }

    @Test
    void позиции_нельзя_удалить_снаружи() {
        var order = Order.builder().customer(customer).items(items).delivery(new CourierDelivery()).build(id);

        assertThatThrownBy(() -> {
            var iterator = order.iterator();
            iterator.next();
            iterator.remove();
        }).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void изменение_исходного_списка_не_меняет_заказ() {
        var source = new ArrayList<>(items);
        var order = Order.builder().customer(customer).items(source).delivery(new CourierDelivery()).build(id);

        source.clear();

        assertThat(order).hasSize(2);
    }

    @Test
    void позиции_обходятся_в_for_each() {
        var order = Order.builder().customer(customer).items(items).delivery(new CourierDelivery()).build(id);
        int quantity = 0;

        for (OrderItem item : order) {
            quantity += item.quantity();
        }

        assertThat(quantity).isEqualTo(3);
    }

    @Test
    void оплаченный_заказ_отправляется() {
        var order = Order.builder().customer(customer).items(items).delivery(new CourierDelivery()).build(id);
        order.pay();
        order.ship();

        assertThat(order.status()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void неоплаченный_заказ_отправить_нельзя() {
        var order = Order.builder().customer(customer).items(items).delivery(new CourierDelivery()).build(id);

        assertThatThrownBy(order::ship)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void дважды_оплатить_нельзя() {
        var order = Order.builder().customer(customer).items(items).delivery(new CourierDelivery()).build(id);
        order.pay();

        assertThatThrownBy(order::pay)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void отправленный_заказ_отменить_нельзя() {
        var order = Order.builder().customer(customer).items(items).delivery(new CourierDelivery()).build(id);
        order.pay();
        order.ship();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void отменённый_заказ_оплатить_нельзя() {
        var order = Order.builder().customer(customer).items(items).delivery(new CourierDelivery()).build(id);
        order.cancel();

        assertThatThrownBy(order::pay)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Нельзя оплатить заказ в статусе CANCELLED");
    }

    @Test
    void заказы_равны_по_идентификатору() {
        var a = Order.builder().customer(customer).items(items).delivery(new CourierDelivery()).build(id);
        var b = Order.builder().customer(customer).items(items).delivery(new ExpressDelivery()).build(id);

        assertThat(a).isEqualTo(b);
    }
}
