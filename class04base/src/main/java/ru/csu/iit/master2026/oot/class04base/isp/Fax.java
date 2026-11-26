package ru.csu.iit.master2026.oot.class04base.isp;

/** Ровно то, что нужно отделу продаж. */
public interface Fax {

    /** @return номер, на который ушёл факс */
    String fax(String document, String number);
}
