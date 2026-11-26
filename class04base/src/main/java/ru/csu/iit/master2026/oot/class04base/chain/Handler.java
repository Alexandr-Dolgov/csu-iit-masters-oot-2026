package ru.csu.iit.master2026.oot.class04base.chain;

/** Конец цепочки, как сервлет: до него запрос доходит, только если все фильтры пропустили. */
public interface Handler {

    void handle(Request request, Response response);
}
