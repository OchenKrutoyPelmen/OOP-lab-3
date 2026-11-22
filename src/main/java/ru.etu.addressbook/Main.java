package ru.etu.addressbook;

import ru.etu.addressbook.ConsoleApp;
import ru.etu.addressbook.parser.AddressParserFactory;
import ru.etu.addressbook.statistics.StatisticsPrinter;
import ru.etu.addressbook.statistics.StatisticsService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        ConsoleApp app = new ConsoleApp(
                new AddressParserFactory(),
                new StatisticsService(),
                new StatisticsPrinter(),
                new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)),
                System.out,
                System.err
        );
        app.run();
    }
}
