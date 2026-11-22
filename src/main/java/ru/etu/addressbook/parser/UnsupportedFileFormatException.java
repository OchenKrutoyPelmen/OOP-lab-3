package ru.etu.addressbook.parser;

import java.nio.file.Path;

public class UnsupportedFileFormatException extends RuntimeException {

    public UnsupportedFileFormatException(Path file) {
        super("Неподдерживаемый формат файла: " + file
                + ". Ожидается .csv или .xml");
    }
}
