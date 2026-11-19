package ru.csu.iit.master2026.oot.class02orderflow.app;

import ru.csu.iit.master2026.oot.class02orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class02orderflow.domain.OrderId;

import java.util.List;
import java.util.Optional;

/** Где хранятся заказы, решает тот, кто собирает приложение, а не сценарии. Роль — эксплуатация. */
public interface OrderRepository {

    OrderId nextId();

    void save(Order order);

    Optional<Order> find(OrderId id);

    List<Order> findAll();
}
