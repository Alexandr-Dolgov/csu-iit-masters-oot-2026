package ru.csu.iit.master2026.oot.class04base.chain;

import java.util.List;

/**
 * Паттерн «Цепочка обязанностей». Новая цепочка на каждый запрос: она помнит, какой фильтр следующий.
 * Так устроены {@code ApplicationFilterChain} в Tomcat и {@code FilterChainProxy} в Spring Security.
 */
public final class SimpleFilterChain implements FilterChain {

    private final List<Filter> filters;
    private final Handler handler;
    private int position = 0;

    public SimpleFilterChain(List<Filter> filters, Handler handler) {
        this.filters = List.copyOf(filters);
        this.handler = handler;
    }

    @Override
    public void doFilter(Request request, Response response) {
        if (position < filters.size()) {
            Filter next = filters.get(position);
            position++;
            next.doFilter(request, response, this);
        } else {
            handler.handle(request, response);
        }
    }
}
