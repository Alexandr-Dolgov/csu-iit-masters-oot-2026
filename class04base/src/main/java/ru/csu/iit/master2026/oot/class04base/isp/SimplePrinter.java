package ru.csu.iit.master2026.oot.class04base.isp;

/** Обычный принтер. Сканера и факса нет, а методы интерфейс требует — остаётся бросать исключение. */
public final class SimplePrinter implements Device {

    @Override
    public String print(String document) {
        return "напечатано: " + document;
    }

    @Override
    public String scan(String paper) {
        throw new UnsupportedOperationException("сканера нет");
    }

    @Override
    public String fax(String document, String number) {
        throw new UnsupportedOperationException("факса нет");
    }
}
