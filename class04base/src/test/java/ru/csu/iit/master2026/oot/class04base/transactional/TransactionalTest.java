package ru.csu.iit.master2026.oot.class04base.transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionalTest {

    private final AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(TransferConfig.class);
    private final TransferService transfers = context.getBean(TransferService.class);
    private final Journal journal = context.getBean(Journal.class);

    @AfterEach
    void close() {
        context.close();
    }

    @Test
    void вместо_бина_наследник_cglib() {
        assertThat(AopUtils.isCglibProxy(transfers)).isTrue();
        assertThat(transfers.getClass().getSuperclass()).isEqualTo(TransferService.class);
    }

    @Test
    void прокси_открывает_и_фиксирует_транзакцию() {
        transfers.transfer("anna", "boris", 500);

        assertThat(journal.entries()).containsExactly(
                "begin", "списано 500 с anna", "зачислено 500 на boris", "commit");
    }

    @Test
    void непроверяемое_исключение_откатывает() {
        assertThatThrownBy(() -> transfers.transfer("anna", "boris", -1))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(journal.entries()).containsExactly("begin", "списано -1 с anna", "rollback");
    }

    @Test
    void проверяемое_исключение_фиксирует_по_умолчанию() {
        assertThatThrownBy(() -> transfers.transferChecked("anna", "boris", 1_000_000))
                .isInstanceOf(LimitExceededException.class);

        // ждали rollback: списание без зачисления зафиксировано. Нужно rollbackFor = Exception.class
        assertThat(journal.entries()).containsExactly("begin", "списано 1000000 с anna", "commit");
    }

    @Test
    void без_контейнера_транзакций_нет() {
        Journal plain = new Journal();

        new TransferService(plain).transfer("anna", "boris", 500);

        assertThat(plain.entries()).containsExactly("списано 500 с anna", "зачислено 500 на boris");
    }

    @Test
    void вызов_через_this_идёт_мимо_прокси_транзакций_нет() {
        transfers.payAll("anna", List.of("boris", "vera"), 100);

        // ждали begin/commit вокруг каждого перевода
        assertThat(journal.entries()).containsExactly(
                "списано 100 с anna", "зачислено 100 на boris",
                "списано 100 с anna", "зачислено 100 на vera");
    }

    @Test
    void вызов_из_другого_бина_идёт_через_прокси() {
        context.getBean(Payroll.class).payAll("anna", List.of("boris", "vera"), 100);

        assertThat(journal.entries()).containsExactly(
                "begin", "списано 100 с anna", "зачислено 100 на boris", "commit",
                "begin", "списано 100 с anna", "зачислено 100 на vera", "commit");
    }
}
