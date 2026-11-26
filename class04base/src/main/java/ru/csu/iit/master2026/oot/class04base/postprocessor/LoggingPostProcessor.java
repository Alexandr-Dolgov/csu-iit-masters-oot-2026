package ru.csu.iit.master2026.oot.class04base.postprocessor;

import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Контейнер отдаёт каждый созданный бин этому объекту, и тот может вернуть вместо бина другой объект.
 * Здесь — прокси с журналом для бинов, помеченных {@link Logged}. Так же Spring подставляет
 * прокси для {@code @Transactional}, {@code @Async}, {@code @Cacheable}.
 */
public final class LoggingPostProcessor implements BeanPostProcessor {

    private final List<String> log = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean.getClass().isAnnotationPresent(Logged.class)) {
            return LoggingProxy.wrap(bean, log);
        }
        return bean;
    }

    public List<String> log() {
        return List.copyOf(log);
    }
}
