package ru.csu.iit.master2026.oot.class04base.chain;

/** Без заголовка Authorization отвечает 401 сам и дальше запрос не передаёт. */
public final class AuthFilter implements Filter {

    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        if (request.header("Authorization") == null) {
            response.send(401, "нужен вход");
            return;
        }
        chain.doFilter(request, response);
    }
}
