package ru.csu.iit.master2026.oot.class04base.chain;

import java.util.List;

/** Пишет запрос до остатка цепочки и ответ — после. */
public final class LoggingFilter implements Filter {

    private final List<String> log;

    public LoggingFilter(List<String> log) {
        this.log = log;
    }

    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        log.add("→ " + request.path());
        chain.doFilter(request, response);
        log.add("← " + response.status());
    }
}
