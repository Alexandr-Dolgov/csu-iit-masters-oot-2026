package ru.csu.iit.master2026.oot.hw01bank;

/** Операция над счетами как объект: её можно выполнить и отменить. Готов, менять не нужно. */
public interface Command {

    /** @throws IllegalStateException если выполнить нельзя; счета при этом не меняются */
    void execute();

    /** @throws IllegalStateException если отменить нельзя; счета при этом не меняются */
    void undo();
}
