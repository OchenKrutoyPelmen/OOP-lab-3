package ru.etu.addressbook.parser;

import ru.etu.addressbook.Address;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CsvAddressParser implements AddressParser {

    private static final char DELIMITER = ';';

    @Override
    public boolean supports(Path file) {
        return file.getFileName().toString()
                .toLowerCase(Locale.ROOT)
                .endsWith(".csv");
    }

    @Override
    public List<Address> parse(Path file) throws IOException {
        List<Address> result = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null) {
                return result;
            }
            header = stripBom(header);

            Map<String, Integer> columns = buildColumnIndex(splitLine(header));
            int cityIdx = require(columns, "city");
            int streetIdx = require(columns, "street");
            int houseIdx = require(columns, "house");
            int floorIdx = require(columns, "floor");
            int minColumns = columns.size();

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                List<String> fields = splitLine(line);
                if (fields.size() < minColumns) {
                    warn("Пропущена строка " + lineNumber + ": недостаточно полей");
                    continue;
                }
                try {
                    result.add(new Address(
                            fields.get(cityIdx).trim(),
                            fields.get(streetIdx).trim(),
                            parseInt(fields.get(houseIdx), lineNumber),
                            parseInt(fields.get(floorIdx), lineNumber)
                    ));
                } catch (RuntimeException e) {
                    warn("Пропущена строка " + lineNumber + ": " + e.getMessage());
                }
            }
        }
        return result;
    }

    private static Map<String, Integer> buildColumnIndex(List<String> header) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < header.size(); i++) {
            index.put(header.get(i).trim().toLowerCase(Locale.ROOT), i);
        }
        return index;
    }

    private static int require(Map<String, Integer> columns, String name) throws IOException {
        Integer idx = columns.get(name);
        if (idx == null) {
            throw new IOException("В CSV отсутствует обязательная колонка: " + name);
        }
        return idx;
    }

    private static int parseInt(String value, int lineNumber) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "не число '" + value + "' в строке " + lineNumber, e);
        }
    }

    private static String stripBom(String s) {
        return !s.isEmpty() && s.charAt(0) == '\uFEFF' ? s.substring(1) : s;
    }

    static List<String> splitLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == DELIMITER && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields;
    }

    private static void warn(String message) {
        System.err.println("[CSV] " + message);
    }
}
