package ru.csu.iit.master2026.oot.class02base.command;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class CommandTest {

    @Test
    void отмена_в_обратном_порядке() {
        Document document = new Document();
        Editor editor = new Editor();

        editor.run(new Append(document, "Hello"));
        editor.run(new Append(document, ", world"));
        editor.undo();

        assertThat(document.text()).isEqualTo("Hello");
    }

    @Test
    void runnable_команда_без_отмены_из_jdk() {
        List<String> log = new ArrayList<>();
        List<Runnable> queue = List.of(() -> log.add("a"), () -> log.add("b"));

        queue.forEach(Runnable::run);   // создали в одном месте, выполнили в другом

        assertThat(log).containsExactly("a", "b");
    }

    @Test
    void executor_выполняет_команды_которые_ему_передали() throws Exception {
        Callable<Integer> answer = () -> 6 * 7;

        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<Integer> result = executor.submit(answer);

            assertThat(result.get()).isEqualTo(42);
        }
    }
}
