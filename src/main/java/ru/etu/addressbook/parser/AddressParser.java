package ru.etu.addressbook.parser;

import ru.etu.addressbook.Address;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface AddressParser {
    boolean supports(Path file);
    List<Address> parse(Path file) throws IOException;
}