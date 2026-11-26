package ru.csu.iit.master2026.oot.class04orderflow.app;

import ru.csu.iit.master2026.oot.class04orderflow.domain.Order;

import java.util.List;

/**
 * Принцип разделения интерфейса: отчётам нужны только прошлые заказы — ни nextId, ни save.
 * Узкий интерфейс вместо обёртки «только для чтения», которая бросала бы исключения (занятие 2).
 */
public interface OrderHistory {

    List<Order> findAll();
}
