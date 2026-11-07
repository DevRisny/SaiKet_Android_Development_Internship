package com.risnysuha.saiket.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A contact read from the phone: its name and every phone number it has. */
public final class Contact {

    private final long id;
    private final String name;
    private final List<String> numbers = new ArrayList<>();

    public Contact(long id, String name) {
        this.id = id;
        this.name = name == null || name.trim().isEmpty() ? "Unknown" : name.trim();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getNumbers() {
        return Collections.unmodifiableList(numbers);
    }

    /** Adds a number unless the same digits are already stored (for example "+94 77 1234567" and "+94771234567"). */
    public boolean addNumber(String number) {
        if (number == null || ContactUtils.digitsOnly(number).isEmpty()) {
            return false;
        }
        String digits = ContactUtils.digitsOnly(number);
        for (String existing : numbers) {
            if (ContactUtils.digitsOnly(existing).equals(digits)) {
                return false;
            }
        }
        numbers.add(number.trim());
        return true;
    }

    public String primaryNumber() {
        return numbers.isEmpty() ? "" : numbers.get(0);
    }
}
