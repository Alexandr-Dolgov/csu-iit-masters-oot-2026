package ru.csu.iit.master2026.oot.class04base.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Склад и подписчики друг о друге не знают; связывает их контейнер. */
@Configuration
public class StockEventsConfig {

    @Bean
    public EventLog eventLog() {
        return new EventLog();
    }

    /** Издатель — сам контекст: ApplicationContext extends ApplicationEventPublisher. */
    @Bean
    public SpringStock stock(ApplicationEventPublisher events) {
        return new SpringStock(events);
    }

    @Bean
    public ShopWindowListener shopWindow(EventLog log) {
        return new ShopWindowListener(log);
    }

    @Bean
    public PurchasingListener purchasing(EventLog log) {
        return new PurchasingListener(log);
    }
}
