package ru.etu.addressbook.parser;

import ru.etu.addressbook.Address;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class XmlAddressParser implements AddressParser {
    @Override
    public boolean supports(Path file) {
        return file.getFileName().toString()
                .toLowerCase(Locale.ROOT)
                .endsWith(".xml");
    }

    @Override
    public List<Address> parse(Path file) throws IOException {
        List<Address> result = new ArrayList<>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        // защита от XXE атак

        try (InputStream in = Files.newInputStream(file)) {
            XMLStreamReader reader = factory.createXMLStreamReader(in);
            try {
                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLStreamConstants.START_ELEMENT
                            && "item".equals(reader.getLocalName())) {
                        try {
                            result.add(new Address(
                                    requiredAttribute(reader, "city"),
                                    requiredAttribute(reader, "street"),
                                    parseInt(requiredAttribute(reader, "house")),
                                    parseInt(requiredAttribute(reader, "floor"))
                            ));
                        } catch (RuntimeException e) {
                            System.err.println("[XML] Пропущен <item>: " + e.getMessage());
                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (XMLStreamException e) {
            throw new IOException("Ошибка разбора XML: " + e.getMessage(), e);
        }
        return result;
    }

    private static String requiredAttribute(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value == null) {
            throw new IllegalArgumentException("отсутствует атрибут '" + name + "'");
        }
        return value;
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("не число '" + value + "'", e);
        }
    }
}