package ru.etu.addressbook.parser;

import java.nio.file.Path;
import java.util.List;

public final class AddressParserFactory {

    private final List<AddressParser> parsers;

    public AddressParserFactory() {
        this(List.of(new CsvAddressParser(), new XmlAddressParser()));
    }

    public AddressParserFactory(List<AddressParser> parsers) {
        this.parsers = List.copyOf(parsers);
    }

    public AddressParser forFile(Path file) {
        return parsers.stream()
                .filter(p -> p.supports(file))
                .findFirst()
                .orElseThrow(() -> new UnsupportedFileFormatException(file));
    }
}
