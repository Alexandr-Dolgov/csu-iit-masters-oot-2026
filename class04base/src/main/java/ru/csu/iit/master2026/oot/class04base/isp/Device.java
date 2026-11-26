package ru.csu.iit.master2026.oot.class04base.isp;

/**
 * Толстый интерфейс: всё, что умеет офисный комбайн. Кто хочет только печатать,
 * всё равно зависит от сканера и факса.
 */
public interface Device {

    /** @return что напечатано */
    String print(String document);

    /** @return распознанный текст */
    String scan(String paper);

    /** @return номер, на который ушёл факс */
    String fax(String document, String number);
}
