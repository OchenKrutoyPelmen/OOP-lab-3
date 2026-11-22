package ru.etu.addressbook.statistics;

import ru.etu.addressbook.Address;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class StatisticsService {

    public StatisticsReport compute(List<Address> addresses, long processingNanos) {
        Map<Address, Integer> counts = new HashMap<>(addresses.size() * 2);
        for (Address a : addresses) {
            counts.merge(a, 1, Integer::sum);
        }

        List<DuplicateEntry> duplicates = counts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(e -> new DuplicateEntry(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingInt(DuplicateEntry::count).reversed()
                        .thenComparing(d -> d.address().city())
                        .thenComparing(d -> d.address().street()))
                .toList();

        Map<String, Map<Integer, Integer>> floorCountsByCity = new TreeMap<>();
        for (Address a : addresses) {
            floorCountsByCity
                    .computeIfAbsent(a.city(), k -> new TreeMap<>())
                    .merge(a.floor(), 1, Integer::sum);
        }

        return new StatisticsReport(duplicates, floorCountsByCity, processingNanos);
    }
}
