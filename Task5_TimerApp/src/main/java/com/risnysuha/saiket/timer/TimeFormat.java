package com.risnysuha.saiket.timer;

import java.util.Locale;

/** Turns milliseconds into the text shown on screen. */
public final class TimeFormat {

    private TimeFormat() {
    }

    /** 65_000 -> "00:01:05". Hours keep growing past 99 if needed. */
    public static String hms(long ms) {
        long totalSeconds = Math.max(0, ms) / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    /** Progress of the ring: how far through the current minute we are, 0 to 1. */
    public static float minuteProgress(long ms) {
        return (Math.max(0, ms) % 60_000) / 60_000f;
    }

    /** "+00:00:12" for the difference between two laps. */
    public static String split(long current, long previous) {
        return "+" + hms(current - previous);
    }
}
