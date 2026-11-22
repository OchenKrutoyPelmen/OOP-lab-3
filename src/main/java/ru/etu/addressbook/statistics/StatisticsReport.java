package ru.etu.addressbook.statistics;

import java.util.List;
import java.util.Map;


public record StatisticsReport(
        List<DuplicateEntry> duplicates,
        Map<String, Map<Integer, Integer>> floorCountsByCity,
        long processingNanos
) {
}
