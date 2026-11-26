package ru.csu.iit.master2026.oot.class04base.transactional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** {@code @EnableTransactionManagement} регистрирует постпроцессор, который оборачивает бины с {@code @Transactional}. */
@Configuration
@EnableTransactionManagement
public class TransferConfig {

    @Bean
    public Journal journal() {
        return new Journal();
    }

    @Bean
    public PlatformTransactionManager transactionManager(Journal journal) {
        return new RecordingTransactionManager(journal);
    }

    @Bean
    public TransferService transferService(Journal journal) {
        return new TransferService(journal);
    }
}
