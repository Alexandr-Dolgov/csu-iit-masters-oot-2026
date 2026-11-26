package ru.csu.iit.master2026.oot.class04base.chain;

import java.util.Map;

/** Запрос, как {@code HttpServletRequest}, только самое нужное. */
public record Request(String path, Map<String, String> headers) {

    public Request {
        headers = Map.copyOf(headers);
    }

    public String header(String name) {
        return headers.get(name);
    }
}
