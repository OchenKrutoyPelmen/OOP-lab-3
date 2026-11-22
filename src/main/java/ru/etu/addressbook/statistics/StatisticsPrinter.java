package ru.etu.addressbook.statistics;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public final class StatisticsPrinter {

    private static final List<Integer> MAIN_FLOORS = List.of(1, 2, 3, 4, 5);

    public void print(StatisticsReport report, PrintStream out) {
        out.println("=== Дублирующиеся записи ===");
        if (report.duplicates().isEmpty()) {
            out.println("Дубликатов не найдено.");
        } else {
            for (DuplicateEntry d : report.duplicates()) {
                out.printf("%s | %s | %d | %d — %d раз(а)%n",
                        d.address().city(),
                        d.address().street(),
                        d.address().house(),
                        d.address().floor(),
                        d.count());
            }
        }

        out.println();
        out.println("=== Количество зданий по этажам в каждом городе ===");
        out.printf("%-20s %6s %6s %6s %6s %6s %7s%n",
                "Город", "1 эт.", "2 эт.", "3 эт.", "4 эт.", "5 эт.", "другое");

        for (Map.Entry<String, Map<Integer, Integer>> entry
                : report.floorCountsByCity().entrySet()) {
            out.printf("%-20s", entry.getKey());
            Map<Integer, Integer> floors = entry.getValue();
            int shown = 0;
            for (int f : MAIN_FLOORS) {
                int c = floors.getOrDefault(f, 0);
                shown += c;
                out.printf(" %6d", c);
            }
            int total = floors.values().stream().mapToInt(Integer::intValue).sum();
            out.printf(" %7d%n", total - shown);
        }

        out.println();
        out.printf("Время обработки файла: %.3f мс%n",
                report.processingNanos() / 1_000_000.0);
    }
}
