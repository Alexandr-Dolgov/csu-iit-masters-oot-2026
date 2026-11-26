package ru.csu.iit.master2026.oot.class04base.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.csu.iit.master2026.oot.class04base.ioc.Invoices;
import ru.csu.iit.master2026.oot.class04base.ioc.Sequence;

/** Конфигурация без прокси: вызов {@code sequence()} — обычный вызов Java, он создаёт новый счётчик. */
@Configuration(proxyBeanMethods = false)
public class LiteInvoiceConfig {

    @Bean
    public Sequence sequence() {
        return new Sequence();
    }

    @Bean
    public Invoices invoices() {
        return new Invoices("INV", sequence());
    }

    @Bean
    public Invoices refunds() {
        return new Invoices("REF", sequence());
    }
}
