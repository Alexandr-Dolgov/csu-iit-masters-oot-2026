package ru.csu.iit.master2026.oot.class03base.proxy;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProxyTest {

    private final WarehousePriceService warehouse =
            new WarehousePriceService(Map.of("kettle", 2490, "mug", 350));

    /** Клиент написан в терминах PriceService: склад перед ним или прокси — не знает. */
    private static int basket(PriceService prices) {
        return prices.price("kettle") + prices.price("mug") + prices.price("mug");
    }

    @Test
    void без_прокси_каждый_вызов_идёт_на_склад() {
        assertThat(basket(warehouse)).isEqualTo(3190);
        assertThat(warehouse.requests()).isEqualTo(3);
    }

    @Test
    void кэширующий_прокси_ходит_на_склад_один_раз_за_товар() {
        PriceService prices = new CachingPriceService(warehouse);

        assertThat(basket(prices)).isEqualTo(3190);
        assertThat(basket(prices)).isEqualTo(3190);
        assertThat(warehouse.requests()).isEqualTo(2);
    }

    @Test
    void динамический_прокси_для_нашего_интерфейса() {
        List<String> log = new ArrayList<>();
        PriceService prices = LoggingProxy.wrap(PriceService.class, warehouse, log);

        basket(prices);

        assertThat(log).containsExactly("price(kettle)", "price(mug)", "price(mug)");
        assertThat(Proxy.isProxyClass(prices.getClass())).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    void тот_же_прокси_для_интерфейса_из_jdk() {
        List<String> log = new ArrayList<>();
        List<String> tags = LoggingProxy.wrap(List.class, new ArrayList<>(), log);

        tags.add("sale");
        tags.size();

        assertThat(log).containsExactly("add(sale)", "size()");
    }

    @Test
    void исключение_настоящего_объекта_проходит_сквозь_прокси() {
        PriceService prices = LoggingProxy.wrap(PriceService.class, warehouse, new ArrayList<>());

        assertThatThrownBy(() -> prices.price("laptop"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
