package ru.csu.iit.master2026.oot.class03base.creation;

/** Выгрузка для бухгалтерии: name;rubles. */
public final class CsvExporter extends Exporter {

    @Override
    protected Formatter createFormatter() {
        return line -> line.name() + ";" + line.rubles();
    }
}
