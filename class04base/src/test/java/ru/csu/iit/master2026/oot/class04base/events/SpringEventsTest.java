package ru.csu.iit.master2026.oot.class04base.events;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpringEventsTest {

    @Test
    void контейнер_доставляет_событие_подписчикам_по_порядку() {
        try (var context = new AnnotationConfigApplicationContext(StockEventsConfig.class)) {
            SpringStock stock = context.getBean(SpringStock.class);

            stock.set("kettle", 10);
            stock.set("kettle", 0);

            String thread = Thread.currentThread().getName();
            assertThat(context.getBean(EventLog.class).entries()).containsExactly(
                    "витрина: скрыть kettle",
                    "закупки: пополнить kettle [" + thread + "]");   // тот же поток: синхронно
        }
    }

    /** Аналитика, которая падает. Order(0) — её вызовут первой. */
    static final class BrokenAnalytics {
        @EventListener
        @Order(0)
        public void on(StockChanged event) {
            throw new IllegalStateException("аналитика недоступна");
        }
    }

    @Configuration
    @Import(StockEventsConfig.class)
    static class WithBrokenAnalytics {
        @Bean
        BrokenAnalytics analytics() {
            return new BrokenAnalytics();
        }
    }

    @Test
    void исключение_подписчика_доходит_до_издателя_остальные_не_узнают() {
        try (var context = new AnnotationConfigApplicationContext(WithBrokenAnalytics.class)) {
            SpringStock stock = context.getBean(SpringStock.class);

            assertThatThrownBy(() -> stock.set("kettle", 0))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(context.getBean(EventLog.class).entries()).isEmpty();
        }
    }

    /** Spring и сам издатель: о своём жизненном цикле сообщает событиями. */
    static final class StartupListener {
        final List<String> seen = new ArrayList<>();

        @EventListener
        public void on(ContextRefreshedEvent event) {
            seen.add("контекст готов");
        }
    }

    @Configuration
    static class WithStartupListener {
        @Bean
        StartupListener startup() {
            return new StartupListener();
        }
    }

    @Test
    void контекст_публикует_свои_события() {
        try (var context = new AnnotationConfigApplicationContext(WithStartupListener.class)) {
            assertThat(context.getBean(StartupListener.class).seen).containsExactly("контекст готов");
        }
    }
}
