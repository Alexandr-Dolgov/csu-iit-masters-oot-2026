package ru.csu.iit.master2026.oot.class02base.command;

import java.util.ArrayDeque;
import java.util.Deque;

/** Вызывающий: выполняет команды и хранит историю. Какие бывают команды — не знает. */
public final class Editor {

    private final Deque<Command> history = new ArrayDeque<>();

    public void run(Command command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }
}
