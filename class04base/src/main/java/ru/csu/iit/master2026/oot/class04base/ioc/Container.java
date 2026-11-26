package ru.csu.iit.master2026.oot.class04base.ioc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Самодельный контейнер: {@code Infrastructure} из занятия 3, обобщённый на любые типы.
 * Сборка регистрирует рецепты, контейнер сам решает, когда создать объект, и создаёт его один раз.
 */
public final class Container {

    private final Map<Class<?>, Function<Container, ?>> recipes = new HashMap<>();
    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final List<String> created = new ArrayList<>();

    /** Рецепт получает контейнер — из него берёт свои зависимости. */
    public <T> void register(Class<T> type, Function<Container, ? extends T> recipe) {
        recipes.put(type, recipe);
    }

    /** Фабричный метод: какой класс создать, знает рецепт, а не тот, кто просит. */
    public <T> T get(Class<T> type) {
        Object instance = instances.get(type);
        if (instance == null) {
            Function<Container, ?> recipe = recipes.get(type);
            if (recipe == null) {
                throw new IllegalStateException("Не зарегистрирован " + type.getSimpleName());
            }
            instance = recipe.apply(this);
            instances.put(type, instance);
            created.add(type.getSimpleName());
        }
        return type.cast(instance);
    }

    /** В каком порядке контейнер создавал объекты. */
    public List<String> created() {
        return List.copyOf(created);
    }
}
