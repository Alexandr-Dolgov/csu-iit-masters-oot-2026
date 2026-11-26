package ru.csu.iit.master2026.oot.class04base.chain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChainOfResponsibilityTest {

    private final List<String> log = new ArrayList<>();
    private final Handler orders = (request, response) -> {
        log.add("обработчик");
        response.send(200, "заказы");
    };
    private final Request anonymous = new Request("/orders", Map.of());
    private final Request signedIn = new Request("/orders", Map.of("Authorization", "Bearer anna"));

    private Response send(Request request, Filter... filters) {
        Response response = new Response();
        new SimpleFilterChain(List.of(filters), orders).doFilter(request, response);
        return response;
    }

    @Test
    void запрос_проходит_все_фильтры_и_доходит_до_обработчика() {
        Response response = send(signedIn, new LoggingFilter(log), new AuthFilter());

        assertThat(response.status()).isEqualTo(200);
        assertThat(log).containsExactly("→ /orders", "обработчик", "← 200");
    }

    @Test
    void фильтр_обрывает_цепочку_обработчик_не_вызван() {
        Response response = send(anonymous, new LoggingFilter(log), new AuthFilter());

        assertThat(response.status()).isEqualTo(401);
        assertThat(log).containsExactly("→ /orders", "← 401");
    }

    @Test
    void порядок_важен_проверка_прав_раньше_журнала_и_отказ_не_записан() {
        Response response = send(anonymous, new AuthFilter(), new LoggingFilter(log));

        assertThat(response.status()).isEqualTo(401);
        assertThat(log).isEmpty();
    }

    @Test
    void новое_звено_без_правки_остальных() {
        Filter maintenance = (request, response, chain) -> response.send(503, "технические работы");

        Response response = send(signedIn, new LoggingFilter(log), maintenance, new AuthFilter());

        assertThat(response.status()).isEqualTo(503);
        assertThat(log).containsExactly("→ /orders", "← 503");
    }

    @Test
    void без_фильтров_запрос_сразу_у_обработчика() {
        assertThat(send(anonymous).status()).isEqualTo(200);
    }
}
