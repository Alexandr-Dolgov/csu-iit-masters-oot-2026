package ru.csu.iit.master2026.oot.class04base.postprocessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** Динамический прокси из занятия 3, но для всех интерфейсов объекта сразу. */
public final class LoggingProxy {

    private LoggingProxy() {
    }

    public static Object wrap(Object target, List<String> log) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                (self, method, args) -> {
                    log.add(method.getName() + "(" + describe(args) + ")");
                    try {
                        return method.invoke(target, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
    }

    private static String describe(Object[] args) {
        return args == null ? "" : Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(", "));
    }
}
