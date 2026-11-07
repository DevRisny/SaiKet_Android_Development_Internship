package com.risnysuha.saiket.activityswitch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Plain-Java helpers for checking and describing a message. Unit tested. */
public final class MessageUtils {

    public static final int MAX_LENGTH = 160;

    public enum Problem { NONE, EMPTY, TOO_LONG }

    private MessageUtils() {
    }

    public static Problem validate(String message) {
        if (message == null || message.trim().isEmpty()) {
            return Problem.EMPTY;
        }
        if (message.trim().length() > MAX_LENGTH) {
            return Problem.TOO_LONG;
        }
        return Problem.NONE;
    }

    public static int wordCount(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    public static int charCount(String text) {
        return text == null ? 0 : text.trim().length();
    }

    /** Uses "Guest" when the sender left the name empty. */
    public static String senderOrDefault(String sender) {
        return sender == null || sender.trim().isEmpty() ? "Guest" : sender.trim();
    }

    public static String formatTime(long epochMillis) {
        return new SimpleDateFormat("hh:mm:ss a", Locale.US).format(new Date(epochMillis));
    }

    /** How long the Intent took to arrive, e.g. "48 ms". Never negative. */
    public static String deliveryTime(long sentAt, long receivedAt) {
        long ms = Math.max(0, receivedAt - sentAt);
        return ms < 1000 ? ms + " ms" : String.format(Locale.US, "%.1f s", ms / 1000f);
    }
}
