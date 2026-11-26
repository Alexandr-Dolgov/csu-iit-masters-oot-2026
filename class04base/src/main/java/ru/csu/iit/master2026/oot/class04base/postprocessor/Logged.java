package ru.csu.iit.master2026.oot.class04base.postprocessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Метка для контейнера: вызовы этого бина записывать в журнал. Сама аннотация ничего не делает. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Logged {
}
