package ru.csu.iit.master2026.oot.class04base.postprocessor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.csu.iit.master2026.oot.class04base.ioc.Sequence;

import java.util.Map;

@Configuration
public class PricesConfig {

    /** static: постпроцессор нужен контейнеру раньше всех остальных бинов. */
    @Bean
    public static LoggingPostProcessor loggingPostProcessor() {
        return new LoggingPostProcessor();
    }

    @Bean
    public PriceService prices() {
        return new WarehousePriceService(Map.of("kettle", 2490, "mug", 350));
    }

    /** Без метки — остаётся как есть. */
    @Bean
    public Sequence sequence() {
        return new Sequence();
    }
}
