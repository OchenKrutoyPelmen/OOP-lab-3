package ru.etu.addressbook;
import java.util.Objects;

public record Address(String city, String street, int house, int floor) {

    public Address {
        Objects.requireNonNull(city, "city");
        Objects.requireNonNull(street, "street");
        if (house < 0) {
            throw new IllegalArgumentException("house must be non-negative: " + house);
        }
        if (floor < 0) {
            throw new IllegalArgumentException("floor must be non-negative: " + floor);
        }
    }
}
