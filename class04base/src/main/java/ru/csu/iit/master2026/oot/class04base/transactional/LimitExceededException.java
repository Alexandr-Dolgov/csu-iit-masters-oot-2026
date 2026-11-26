package ru.csu.iit.master2026.oot.class04base.transactional;

/** Проверяемое исключение. */
public class LimitExceededException extends Exception {

    public LimitExceededException(String message) {
        super(message);
    }
}
