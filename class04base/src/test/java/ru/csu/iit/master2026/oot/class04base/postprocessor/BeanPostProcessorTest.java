package ru.csu.iit.master2026.oot.class04base.postprocessor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.csu.iit.master2026.oot.class04base.ioc.Sequence;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BeanPostProcessorTest {

    /** Клиент из занятия 3: склад перед ним или прокси — не знает. */
    private static int basket(PriceService prices) {
        return prices.price("kettle") + prices.price("mug") + prices.price("mug");
    }

    @Test
    void контейнер_отдаёт_прокси_вместо_бина() {
        try (var context = new AnnotationConfigApplicationContext(PricesConfig.class)) {
            PriceService prices = context.getBean(PriceService.class);

            assertThat(basket(prices)).isEqualTo(3190);
            assertThat(Proxy.isProxyClass(prices.getClass())).isTrue();
            assertThat(context.getBean(LoggingPostProcessor.class).log())
                    .containsExactly("price(kettle)", "price(mug)", "price(mug)");
        }
    }

    @Test
    void бин_без_метки_не_обёрнут() {
        try (var context = new AnnotationConfigApplicationContext(PricesConfig.class)) {
            assertThat(context.getBean(Sequence.class).getClass()).isEqualTo(Sequence.class);
        }
    }

    @Test
    void по_классу_бин_больше_не_найти_прокси_реализует_только_интерфейс() {
        try (var context = new AnnotationConfigApplicationContext(PricesConfig.class)) {
            // прокси — не WarehousePriceService. Отсюда совет: зависеть от интерфейса
            assertThatThrownBy(() -> context.getBean(WarehousePriceService.class))
                    .isInstanceOf(NoSuchBeanDefinitionException.class);
        }
    }

    @Test
    void исключение_бина_проходит_сквозь_прокси() {
        try (var context = new AnnotationConfigApplicationContext(PricesConfig.class)) {
            PriceService prices = context.getBean(PriceService.class);

            assertThatThrownBy(() -> prices.price("laptop"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
