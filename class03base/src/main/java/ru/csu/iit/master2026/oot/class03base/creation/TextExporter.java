package ru.csu.iit.master2026.oot.class03base.creation;

/** Выгрузка для человека: строки пронумерованы. */
public final class TextExporter extends Exporter {

    @Override
    protected Formatter createFormatter() {
        return new NumberingFormatter();
    }

    /** Помнит номер строки — поэтому на каждую выгрузку нужен новый. */
    private static final class NumberingFormatter implements Formatter {

        private int number = 0;

        @Override
        public String format(Line line) {
            number++;
            return number + ". " + line.name() + " — " + line.rubles() + " руб.";
        }
    }
}
