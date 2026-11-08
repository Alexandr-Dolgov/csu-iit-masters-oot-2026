package ru.csu.iit.master2026.oot.class01orderflow.legacy;

import ru.csu.iit.master2026.oot.class01orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class01orderflow.domain.OrderId;

import java.util.LinkedHashMap;
import java.util.Map;

/** «База данных» стартового проекта: заказы по идентификатору. */
class InMemoryStore {
    final Map<OrderId, Order> orders = new LinkedHashMap<>();
}
