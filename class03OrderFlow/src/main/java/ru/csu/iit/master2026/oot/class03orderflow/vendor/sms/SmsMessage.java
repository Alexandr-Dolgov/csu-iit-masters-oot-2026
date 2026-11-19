package ru.csu.iit.master2026.oot.class03orderflow.vendor.sms;

/**
 * Сообщение для шлюза.
 *
 * @param msisdn номер абонента: 11 цифр, без «+», например 79000000001
 * @param body   текст, не длиннее {@value #MAX_LENGTH} символов
 */
public record SmsMessage(String msisdn, String body) {

    public static final int MAX_LENGTH = 160;

    public SmsMessage {
        if (msisdn == null || !msisdn.matches("[0-9]{11}")) {
            throw new IllegalArgumentException("MSISDN — 11 цифр без +: " + msisdn);
        }
        if (body == null || body.isBlank() || body.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Текст от 1 до " + MAX_LENGTH + " символов");
        }
    }
}
