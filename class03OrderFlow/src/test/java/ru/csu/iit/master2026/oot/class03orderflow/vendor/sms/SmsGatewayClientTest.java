package ru.csu.iit.master2026.oot.class03orderflow.vendor.sms;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Как ведёт себя чужая библиотека: тесты-документация, на них опирается адаптер. */
class SmsGatewayClientTest {

    private final SmsMessage hello = new SmsMessage("79000000001", "Заказ ORD-1 принят");

    @Test
    void надёжный_шлюз_выдаёт_идентификаторы() throws GatewayTimeoutException {
        SmsGatewayClient gateway = new SmsGatewayClient(0);

        assertThat(gateway.submit(hello)).isEqualTo("MSG-1");
        assertThat(gateway.submit(hello)).isEqualTo("MSG-2");
    }

    @Test
    void каждый_второй_вызов_таймаут() throws GatewayTimeoutException {
        SmsGatewayClient gateway = new SmsGatewayClient(2);

        assertThat(gateway.submit(hello)).isEqualTo("MSG-1");
        assertThatThrownBy(() -> gateway.submit(hello))
                .isInstanceOf(GatewayTimeoutException.class);
        // повтор проходит: шлюз ничего не помнит о сбое
        assertThat(gateway.submit(hello)).isEqualTo("MSG-2");
    }

    @Test
    void номер_без_плюса() {
        assertThatThrownBy(() -> new SmsMessage("+79000000001", "текст"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void текст_не_длиннее_160() {
        assertThatThrownBy(() -> new SmsMessage("79000000001", "я".repeat(161)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
