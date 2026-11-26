package ru.csu.iit.master2026.oot.class04base.transactional;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

/** Вместо базы данных — журнал: видно, когда прокси открывает, фиксирует и откатывает транзакцию. */
public final class RecordingTransactionManager implements PlatformTransactionManager {

    private final Journal journal;

    public RecordingTransactionManager(Journal journal) {
        this.journal = journal;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) {
        journal.add("begin");
        return new SimpleTransactionStatus();
    }

    @Override
    public void commit(TransactionStatus status) {
        journal.add("commit");
    }

    @Override
    public void rollback(TransactionStatus status) {
        journal.add("rollback");
    }
}
