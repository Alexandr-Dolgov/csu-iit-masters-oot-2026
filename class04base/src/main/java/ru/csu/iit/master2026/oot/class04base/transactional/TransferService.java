package ru.csu.iit.master2026.oot.class04base.transactional;

import org.springframework.transaction.annotation.Transactional;

/**
 * Код сервиса о транзакциях не знает: ни begin, ни commit. Их добавит прокси, который Spring
 * подставит вместо этого бина. Класс не final — прокси будет его наследником (CGLIB).
 */
public class TransferService {

    private static final int LIMIT = 100_000;

    private final Journal journal;

    public TransferService(Journal journal) {
        this.journal = journal;
    }

    @Transactional
    public void transfer(String from, String to, int amount) {
        journal.add("списано " + amount + " с " + from);
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной: " + amount);
        }
        journal.add("зачислено " + amount + " на " + to);
    }

    @Transactional
    public void transferChecked(String from, String to, int amount) throws LimitExceededException {
        journal.add("списано " + amount + " с " + from);
        if (amount > LIMIT) {
            throw new LimitExceededException("Больше лимита: " + amount);
        }
        journal.add("зачислено " + amount + " на " + to);
    }
}
