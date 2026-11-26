package ru.csu.iit.master2026.oot.class04base.isp;

/** Обычный принтер обещает только то, что умеет. */
public final class SimplePrinter implements Printer {

    @Override
    public String print(String document) {
        return "напечатано: " + document;
    }
}
