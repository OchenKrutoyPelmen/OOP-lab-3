package ru.etu.addressbook;

import ru.etu.addressbook.Address;
import ru.etu.addressbook.parser.AddressParser;
import ru.etu.addressbook.parser.AddressParserFactory;
import ru.etu.addressbook.parser.UnsupportedFileFormatException;
import ru.etu.addressbook.statistics.StatisticsPrinter;
import ru.etu.addressbook.statistics.StatisticsReport;
import ru.etu.addressbook.statistics.StatisticsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

public final class ConsoleApp {

    private static final String EXIT_COMMAND = "exit";

    private final AddressParserFactory parserFactory;
    private final StatisticsService statisticsService;
    private final StatisticsPrinter printer;
    private final BufferedReader in;
    private final PrintStream out;
    private final PrintStream err;

    public ConsoleApp(AddressParserFactory parserFactory,
                      StatisticsService statisticsService,
                      StatisticsPrinter printer,
                      BufferedReader in,
                      PrintStream out,
                      PrintStream err) {
        this.parserFactory = parserFactory;
        this.statisticsService = statisticsService;
        this.printer = printer;
        this.in = in;
        this.out = out;
        this.err = err;
    }

    public void run() {
        out.println("Введите путь до файла-справочника (.csv или .xml).");
        out.println("Для выхода введите '" + EXIT_COMMAND + "' или нажмите Ctrl+D.");

        while (true) {
            out.print("> ");
            out.flush();

            String raw;
            try {
                raw = in.readLine();
            } catch (IOException e) {
                err.println("Ошибка чтения ввода: " + e.getMessage());
                return;
            }

            if (raw == null) {
                out.println();
                out.println("Завершение работы.");
                return;
            }

            String input = raw.trim();
            if (input.isEmpty()) {
                continue;
            }
            if (EXIT_COMMAND.equalsIgnoreCase(input)) {
                out.println("Завершение работы.");
                return;
            }

            try {
                handleFile(Path.of(input));
            } catch (InvalidPathException e) {
                err.println("Некорректный путь: " + e.getMessage());
            }
            out.println();
        }
    }

    private void handleFile(Path path) {
        if (!Files.isRegularFile(path)) {
            err.println("Файл не найден или не является обычным файлом: " + path);
            return;
        }

        try {
            long start = System.nanoTime();
            AddressParser parser = parserFactory.forFile(path);
            List<Address> addresses = parser.parse(path);
            long elapsed = System.nanoTime() - start;

            StatisticsReport report = statisticsService.compute(addresses, elapsed);
            printer.print(report, out);
        } catch (UnsupportedFileFormatException e) {
            err.println(e.getMessage());
        } catch (IOException e) {
            err.println("Ошибка чтения файла: " + e.getMessage());
        } catch (RuntimeException e) {
            err.println("Непредвиденная ошибка обработки: " + e.getMessage());
        }
    }
}