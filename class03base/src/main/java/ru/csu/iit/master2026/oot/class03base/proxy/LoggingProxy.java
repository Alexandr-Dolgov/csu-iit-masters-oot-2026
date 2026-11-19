package ru.csu.iit.master2026.oot.class03base.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Динамический прокси: класс-обёртку создаёт JDK во время выполнения,
 * для любого интерфейса. Так устроены Spring AOP и Mockito для интерфейсов.
 */
public final class LoggingProxy {

    private LoggingProxy() {
    }

    public static <T> T wrap(Class<T> type, T target, List<String> log) {
        Object proxy = Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                (self, method, args) -> {
                    log.add(method.getName() + "(" + describe(args) + ")");
                    try {
                        return method.invoke(target, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();   // исключение настоящего объекта — как есть
                    }
                });
        return type.cast(proxy);
    }

    private static String describe(Object[] args) {
        return args == null ? "" : Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(", "));
    }
}
