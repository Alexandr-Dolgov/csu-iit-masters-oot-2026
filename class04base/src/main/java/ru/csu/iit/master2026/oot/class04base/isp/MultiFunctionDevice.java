package ru.csu.iit.master2026.oot.class04base.isp;

/** Комбайн реализует все три роли. Каждый клиент видит его через свою. */
public final class MultiFunctionDevice implements Printer, Scanner, Fax {

    @Override
    public String print(String document) {
        return "напечатано: " + document;
    }

    @Override
    public String scan(String paper) {
        return "отсканировано: " + paper;
    }

    @Override
    public String fax(String document, String number) {
        return number;
    }
}
