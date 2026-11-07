package com.risnysuha.saiket.contacts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ContactUtilsTest {

    private static Contact contact(long id, String name, String... numbers) {
        Contact c = new Contact(id, name);
        for (String n : numbers) {
            c.addNumber(n);
        }
        return c;
    }

    @Test
    public void initialsUseFirstTwoWords() {
        assertEquals("RS", ContactUtils.initials("Risny Suha"));
        assertEquals("JR", ContactUtils.initials("  j. risny suha "));
        assertEquals("M", ContactUtils.initials("madonna"));
        assertEquals("#", ContactUtils.initials("   "));
        assertEquals("#", ContactUtils.initials(null));
    }

    @Test
    public void colourIsStableAndInRange() {
        int a = ContactUtils.colorIndex("Amal Perera", 6);
        assertEquals(a, ContactUtils.colorIndex("amal perera ", 6));
        assertTrue(a >= 0 && a < 6);
        assertEquals(0, ContactUtils.colorIndex("x", 0));
    }

    @Test
    public void sameNumberWithDifferentSpacingIsStoredOnce() {
        Contact c = contact(1, "Ayesha", "+94 77 123 4567", "+94771234567", "077-999-0000");
        assertEquals(2, c.getNumbers().size());
        assertFalse(c.addNumber("   "));
    }

    @Test
    public void filterMatchesNameIgnoringCase() {
        List<Contact> all = Arrays.asList(contact(1, "Amal Perera", "0771111111"),
                contact(2, "Fathima Rizna", "0772222222"), contact(3, "Kumar", "0113333333"));
        assertEquals(1, ContactUtils.filter(all, "fath").size());
        assertEquals("Fathima Rizna", ContactUtils.filter(all, "RIZ").get(0).getName());
    }

    @Test
    public void filterMatchesDigitsOfANumber() {
        List<Contact> all = Arrays.asList(contact(1, "Amal", "+94 77 111 1111"), contact(2, "Kumar", "011 333 3333"));
        List<Contact> found = ContactUtils.filter(all, "333");
        assertEquals(1, found.size());
        assertEquals("Kumar", found.get(0).getName());
    }

    @Test
    public void emptyQueryReturnsEveryone() {
        List<Contact> all = Arrays.asList(contact(1, "A", "1"), contact(2, "B", "2"));
        assertEquals(2, ContactUtils.filter(all, "  ").size());
    }

    @Test
    public void summaryShowsExtraNumbers() {
        assertEquals("0771", ContactUtils.numberSummary(contact(1, "A", "0771")));
        assertEquals("0771  ·  +1 more", ContactUtils.numberSummary(contact(1, "A", "0771", "0112")));
        assertEquals("No number", ContactUtils.numberSummary(contact(1, "A")));
    }

    @Test
    public void blankNameBecomesUnknown() {
        assertEquals("Unknown", new Contact(9, " ").getName());
    }
}
