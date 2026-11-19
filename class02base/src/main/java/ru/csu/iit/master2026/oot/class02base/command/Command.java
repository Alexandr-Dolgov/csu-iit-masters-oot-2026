package ru.csu.iit.master2026.oot.class02base.command;

/** Запрос как объект: его можно сохранить, поставить в очередь, отменить. */
public interface Command {

    void execute();

    void undo();
}
