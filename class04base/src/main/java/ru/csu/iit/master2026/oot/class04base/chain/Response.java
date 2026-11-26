package ru.csu.iit.master2026.oot.class04base.chain;

/** Ответ, как {@code HttpServletResponse}: его заполняет тот, кто обработал запрос. */
public final class Response {

    private int status = 0;
    private String body = "";

    public void send(int status, String body) {
        this.status = status;
        this.body = body;
    }

    public int status() {
        return status;
    }

    public String body() {
        return body;
    }
}
