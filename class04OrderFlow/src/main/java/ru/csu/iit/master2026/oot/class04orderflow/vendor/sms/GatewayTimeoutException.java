package ru.csu.iit.master2026.oot.class04orderflow.vendor.sms;

/** Шлюз не ответил вовремя. Сообщение не отправлено; можно попробовать ещё раз. */
public final class GatewayTimeoutException extends Exception {

    public GatewayTimeoutException(String message) {
        super(message);
    }
}
