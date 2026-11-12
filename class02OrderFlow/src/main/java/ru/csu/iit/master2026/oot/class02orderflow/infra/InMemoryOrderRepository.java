package ru.csu.iit.master2026.oot.class02orderflow.infra;

import ru.csu.iit.master2026.oot.class02orderflow.app.OrderRepository;
import ru.csu.iit.master2026.oot.class02orderflow.domain.Order;
import ru.csu.iit.master2026.oot.class02orderflow.domain.OrderId;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Бывший legacy.InMemoryStore: та же «база» в памяти, но за интерфейсом. */
public final class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> orders = new LinkedHashMap<>();
    private int counter = 0;

    @Override
    public OrderId nextId() {
        counter++;
        return new OrderId("ORD-" + counter);
    }

    @Override
    public void save(Order order) {
        orders.put(order.id(), order);
    }

    @Override
    public Optional<Order> find(OrderId id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return List.copyOf(orders.values());
    }
}
