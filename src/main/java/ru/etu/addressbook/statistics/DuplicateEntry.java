package ru.etu.addressbook.statistics;

import ru.etu.addressbook.Address;

public record DuplicateEntry(Address address, int count) {
}
