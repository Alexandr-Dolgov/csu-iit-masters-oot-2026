package ru.csu.iit.master2026.oot.class01orderflow.domain;

/**
 * Тариф доставки: сколько стоит и сколько дней везти.
 *
 * <p>Новый тариф — новый класс; код, который считает доставку, при этом не меняется.
 * Подробно о полиморфизме — на занятии 2.
 */
public interface DeliveryTariff {

    Money cost(Money goods);

    int days();
}
