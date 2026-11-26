package ru.csu.iit.master2026.oot.class04base.chain;

/** Остаток цепочки, как {@code jakarta.servlet.FilterChain}. Фильтр не знает, кто следующий. */
public interface FilterChain {

    void doFilter(Request request, Response response);
}
