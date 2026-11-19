package ru.csu.iit.master2026.oot.class03base.creation;

import java.util.List;

/**
 * Паттерн «Фабричный метод»: export не знает, какой форматтер ему нужен, —
 * создать его поручено подклассу. Сам export — шаблонный метод из занятия 2.
 */
public abstract class Exporter {

    public final String export(List<Line> lines) {
        Formatter formatter = createFormatter();
        StringBuilder out = new StringBuilder();
        for (Line line : lines) {
            out.append(formatter.format(line)).append('\n');
        }
        return out.toString();
    }

    /** Фабричный метод: новый продукт на каждую выгрузку. */
    protected abstract Formatter createFormatter();
}
