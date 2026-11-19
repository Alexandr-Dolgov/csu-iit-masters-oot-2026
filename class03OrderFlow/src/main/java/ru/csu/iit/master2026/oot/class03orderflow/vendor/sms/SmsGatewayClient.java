package ru.csu.iit.master2026.oot.class03orderflow.vendor.sms;

/**
 * Клиент SMS-шлюза. Вместо сети — консоль; сбои шлюза имитируются детерминированно:
 * каждый {@code failEvery}-й вызов {@link #submit} заканчивается таймаутом.
 */
public final class SmsGatewayClient {

    private final int failEvery;
    private int calls = 0;
    private int sent = 0;

    /** @param failEvery каждый какой вызов падает; 0 — шлюз не падает никогда */
    public SmsGatewayClient(int failEvery) {
        if (failEvery < 0) {
            throw new IllegalArgumentException("failEvery >= 0: " + failEvery);
        }
        this.failEvery = failEvery;
    }

    /** @return идентификатор сообщения в шлюзе, например {@code MSG-1} */
    public String submit(SmsMessage message) throws GatewayTimeoutException {
        calls++;
        if (failEvery > 0 && calls % failEvery == 0) {
            throw new GatewayTimeoutException("Шлюз не ответил за 5 с, вызов " + calls);
        }
        sent++;
        System.out.println("[SMS-GW " + message.msisdn() + "] " + message.body());
        return "MSG-" + sent;
    }
}
