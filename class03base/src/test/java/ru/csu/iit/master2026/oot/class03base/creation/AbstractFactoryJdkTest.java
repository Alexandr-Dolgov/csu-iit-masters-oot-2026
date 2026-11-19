package ru.csu.iit.master2026.oot.class03base.creation;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/** Абстрактная фабрика в JDK: клиент знает только интерфейсы, семейство реализаций выбирает фабрика. */
class AbstractFactoryJdkTest {

    private static final String XML = "<order id=\"ORD-1\"><item>Чайник</item></order>";

    @Test
    void клиент_пишет_в_терминах_javax_xml_и_org_w3c() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(
                new ByteArrayInputStream(XML.getBytes(StandardCharsets.UTF_8)));

        assertThat(document.getDocumentElement().getAttribute("id")).isEqualTo("ORD-1");
    }

    @Test
    void реализации_одного_семейства_и_скрыты_от_клиента() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        // Xerces, встроенный в JDK: фабрика, строитель и документ — из одного пакета
        assertThat(factory.getClass().getPackageName())
                .isEqualTo("com.sun.org.apache.xerces.internal.jaxp");
        assertThat(builder.getClass().getPackageName())
                .isEqualTo("com.sun.org.apache.xerces.internal.jaxp");
        assertThat(document.getClass().getPackageName())
                .isEqualTo("com.sun.org.apache.xerces.internal.dom");
    }
}
