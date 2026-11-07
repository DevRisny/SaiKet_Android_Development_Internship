package com.risnysuha.saiket.contacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Small plain-Java helpers for the contact list. Kept free of Android code so they are unit tested. */
public final class ContactUtils {

    private ContactUtils() {
    }

    /** "Risny Suha" -> "RS", "madonna" -> "M", "  " -> "#". */
    public static String initials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "#";
        }
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            int cp = part.codePointAt(0);
            if (Character.isLetterOrDigit(cp)) {
                sb.appendCodePoint(Character.toUpperCase(cp));
            }
            if (sb.length() >= 2) {
                break;
            }
        }
        return sb.length() == 0 ? "#" : sb.toString();
    }

    /** Picks a stable avatar colour for a name, so the same person always gets the same colour. */
    public static int colorIndex(String name, int paletteSize) {
        if (paletteSize <= 0) {
            return 0;
        }
        String key = name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
        return Math.floorMod(key.hashCode(), paletteSize);
    }

    public static String digitsOnly(String number) {
        return number == null ? "" : number.replaceAll("[^0-9]", "");
    }

    /** Case-insensitive search on the name, or on the digits of any number. */
    public static List<Contact> filter(List<Contact> all, String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(all);
        }
        String q = query.trim().toLowerCase(Locale.ROOT);
        String qDigits = digitsOnly(q);
        List<Contact> result = new ArrayList<>();
        for (Contact c : all) {
            if (c.getName().toLowerCase(Locale.ROOT).contains(q)) {
                result.add(c);
                continue;
            }
            if (!qDigits.isEmpty()) {
                for (String n : c.getNumbers()) {
                    if (digitsOnly(n).contains(qDigits)) {
                        result.add(c);
                        break;
                    }
                }
            }
        }
        return result;
    }

    /** "+94 77 123 4567" or "+94 77 123 4567 · +1 more". */
    public static String numberSummary(Contact contact) {
        int extra = contact.getNumbers().size() - 1;
        if (contact.getNumbers().isEmpty()) {
            return "No number";
        }
        return extra > 0 ? contact.primaryNumber() + "  ·  +" + extra + " more" : contact.primaryNumber();
    }
}
