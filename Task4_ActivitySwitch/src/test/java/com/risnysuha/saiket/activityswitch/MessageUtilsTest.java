package com.risnysuha.saiket.activityswitch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MessageUtilsTest {

    @Test
    public void emptyOrBlankMessageIsRejected() {
        assertEquals(MessageUtils.Problem.EMPTY, MessageUtils.validate(null));
        assertEquals(MessageUtils.Problem.EMPTY, MessageUtils.validate("   "));
    }

    @Test
    public void tooLongMessageIsRejected() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= MessageUtils.MAX_LENGTH; i++) {
            sb.append('a');
        }
        assertEquals(MessageUtils.Problem.TOO_LONG, MessageUtils.validate(sb.toString()));
    }

    @Test
    public void normalMessageIsAccepted() {
        assertEquals(MessageUtils.Problem.NONE, MessageUtils.validate("Hello from Activity One!"));
    }

    @Test
    public void wordsAndCharactersAreCounted() {
        assertEquals(4, MessageUtils.wordCount("  Hello   from Activity One "));
        assertEquals(0, MessageUtils.wordCount(""));
        assertEquals(5, MessageUtils.charCount(" Hello "));
    }

    @Test
    public void blankSenderBecomesGuest() {
        assertEquals("Guest", MessageUtils.senderOrDefault("  "));
        assertEquals("Risny", MessageUtils.senderOrDefault(" Risny "));
    }

    @Test
    public void deliveryTimeIsReadableAndNeverNegative() {
        assertEquals("48 ms", MessageUtils.deliveryTime(1000, 1048));
        assertEquals("1.5 s", MessageUtils.deliveryTime(0, 1500));
        assertEquals("0 ms", MessageUtils.deliveryTime(2000, 1000));
    }

    @Test
    public void timeIsFormattedWithSeconds() {
        assertTrue(MessageUtils.formatTime(0).matches("\\d{2}:\\d{2}:\\d{2} (AM|PM)"));
    }
}
