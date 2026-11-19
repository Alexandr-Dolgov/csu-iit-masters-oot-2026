package ru.csu.iit.master2026.oot.class03orderflow.app;

/**
 * Паттерн «Абстрактная фабрика»: семейство зависимостей, которые нужны сценариям.
 * Члены семейства согласованы: все сценарии работают с одним и тем же хранилищем.
 * Какое семейство взять — в памяти, в базе, тестовое, — решает тот, кто собирает приложение.
 */
public interface Infrastructure {

    OrderRepository orders();

    Notifier notifier();
}
