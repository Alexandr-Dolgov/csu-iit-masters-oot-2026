package ru.csu.iit.master2026.oot.class01orderflow.legacy;

import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderId;

import java.util.LinkedHashMap;
import java.util.Map;

/** «База данных» стартового проекта: строки заказов по идентификатору. */
class InMemoryStore {
    final Map<OrderId, Object[]> orders = new LinkedHashMap<>();
}
