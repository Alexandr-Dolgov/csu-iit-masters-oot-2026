package ru.csu.iit.master2026.oot.class04base.chain;

/**
 * Звено цепочки, как {@code jakarta.servlet.Filter}. Фильтр сам решает: обработать запрос
 * и остановиться или передать дальше — {@code chain.doFilter(request, response)}.
 */
public interface Filter {

    void doFilter(Request request, Response response, FilterChain chain);
}
