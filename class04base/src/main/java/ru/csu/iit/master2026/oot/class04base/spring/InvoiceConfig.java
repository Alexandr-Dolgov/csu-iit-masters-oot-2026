package ru.csu.iit.master2026.oot.class04base.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.csu.iit.master2026.oot.class04base.ioc.Invoices;
import ru.csu.iit.master2026.oot.class04base.ioc.Sequence;

/**
 * Те же рецепты, что в самодельном {@code Container}, только записаны методами {@code @Bean}.
 * Классы {@link Sequence} и {@link Invoices} о Spring не знают.
 */
@Configuration
public class InvoiceConfig {

    @Bean
    public Sequence sequence() {
        return new Sequence();
    }

    /** Зависимость — вызовом метода. Spring перехватит вызов и вернёт готовый бин. */
    @Bean
    public Invoices invoices() {
        return new Invoices("INV", sequence());
    }

    /** Зависимость — параметром: её передаст контейнер. */
    @Bean
    public Invoices refunds(Sequence sequence) {
        return new Invoices("REF", sequence);
    }

    /** Черновики: новый экземпляр со своим счётчиком на каждый запрос. */
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Invoices drafts() {
        return new Invoices("DRAFT", new Sequence());
    }
}
