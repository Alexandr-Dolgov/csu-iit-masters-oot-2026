package ru.csu.iit.master2026.oot.class02orderflow.domain;

/**
 * Тариф доставки: сколько стоит и сколько дней везти.
 *
 * <p>Паттерн «Стратегия»: {@link Order} — контекст, тариф — сменяемый алгоритм.
 * Новый тариф — новый класс; код, который считает доставку, при этом не меняется.
 */
public interface DeliveryTariff {

    Money cost(Money goods);

    int days();
}
