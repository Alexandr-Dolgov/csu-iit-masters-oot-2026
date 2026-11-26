package ru.csu.iit.master2026.oot.class04base.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.csu.iit.master2026.oot.class04base.ioc.Invoices;
import ru.csu.iit.master2026.oot.class04base.ioc.Sequence;

import static org.assertj.core.api.Assertions.assertThat;

class SpringContainerTest {

    @Test
    void контекст_создаёт_бин_и_его_зависимости() {
        try (var context = new AnnotationConfigApplicationContext(InvoiceConfig.class)) {
            // клиент знает только интерфейс фабрики
            BeanFactory beans = context;
            Invoices invoices = beans.getBean("invoices", Invoices.class);

            assertThat(invoices.issue()).isEqualTo("INV-1");
            assertThat(invoices.issue()).isEqualTo("INV-2");
        }
    }

    @Test
    void singleton_по_умолчанию_один_экземпляр_на_контекст() {
        try (var context = new AnnotationConfigApplicationContext(InvoiceConfig.class)) {
            assertThat(context.getBean("sequence")).isSameAs(context.getBean("sequence"));
        }
    }

    @Test
    void у_каждого_контекста_свои_бины() {
        try (var first = new AnnotationConfigApplicationContext(InvoiceConfig.class);
             var second = new AnnotationConfigApplicationContext(InvoiceConfig.class)) {
            assertThat(first.getBean("sequence")).isNotSameAs(second.getBean("sequence"));
        }
    }

    @Test
    void prototype_новый_экземпляр_на_каждый_запрос() {
        try (var context = new AnnotationConfigApplicationContext(InvoiceConfig.class)) {
            Invoices first = context.getBean("drafts", Invoices.class);
            Invoices second = context.getBean("drafts", Invoices.class);

            assertThat(first).isNotSameAs(second);
            assertThat(first.issue()).isEqualTo("DRAFT-1");
            assertThat(second.issue()).isEqualTo("DRAFT-1");
        }
    }

    @Test
    void вызов_bean_метода_и_параметр_дают_один_и_тот_же_счётчик() {
        try (var context = new AnnotationConfigApplicationContext(InvoiceConfig.class)) {
            Invoices invoices = context.getBean("invoices", Invoices.class);
            Invoices refunds = context.getBean("refunds", Invoices.class);

            assertThat(invoices.issue()).isEqualTo("INV-1");
            assertThat(refunds.issue()).isEqualTo("REF-2");   // счётчик общий
        }
    }

    @Test
    void конфигурация_подменена_наследником_cglib() {
        try (var context = new AnnotationConfigApplicationContext(InvoiceConfig.class)) {
            Class<?> config = context.getBean(InvoiceConfig.class).getClass();

            assertThat(config.getName()).contains("$$SpringCGLIB$$");
            assertThat(config.getSuperclass()).isEqualTo(InvoiceConfig.class);
        }
    }

    @Test
    void без_прокси_вызов_bean_метода_создаёт_второй_счётчик() {
        try (var context = new AnnotationConfigApplicationContext(LiteInvoiceConfig.class)) {
            Invoices invoices = context.getBean("invoices", Invoices.class);
            Invoices refunds = context.getBean("refunds", Invoices.class);

            assertThat(invoices.issue()).isEqualTo("INV-1");
            assertThat(refunds.issue()).isEqualTo("REF-1");   // ждали REF-2
            assertThat(context.getBean(LiteInvoiceConfig.class).getClass())
                    .isEqualTo(LiteInvoiceConfig.class);
        }
    }

    @Test
    void бин_sequence_это_обычный_объект() {
        try (var context = new AnnotationConfigApplicationContext(InvoiceConfig.class)) {
            assertThat(context.getBean("sequence").getClass()).isEqualTo(Sequence.class);
        }
    }
}
