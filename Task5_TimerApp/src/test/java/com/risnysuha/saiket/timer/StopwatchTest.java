package com.risnysuha.saiket.timer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StopwatchTest {

    @Test
    public void startsAtZeroAndReady() {
        Stopwatch sw = new Stopwatch();
        assertTrue(sw.isReset());
        assertFalse(sw.isRunning());
        assertEquals(0, sw.elapsed(5_000));
    }

    @Test
    public void countsWhileRunning() {
        Stopwatch sw = new Stopwatch();
        sw.start(1_000);
        assertTrue(sw.isRunning());
        assertEquals(3_500, sw.elapsed(4_500));
    }

    @Test
    public void pauseFreezesTimeAndResumeContinues() {
        Stopwatch sw = new Stopwatch();
        sw.start(0);
        sw.pause(2_000);
        assertEquals(2_000, sw.elapsed(10_000));
        sw.start(10_000);
        assertEquals(5_000, sw.elapsed(13_000));
    }

    @Test
    public void resetStopsAndGoesBackToZero() {
        Stopwatch sw = new Stopwatch();
        sw.start(0);
        sw.lap(1_000);
        sw.reset();
        assertFalse(sw.isRunning());
        assertTrue(sw.isReset());
        assertEquals(0, sw.elapsed(9_000));
        assertTrue(sw.getLaps().isEmpty());
    }

    @Test
    public void startingTwiceDoesNotRestartTheClock() {
        Stopwatch sw = new Stopwatch();
        sw.start(0);
        sw.start(3_000);
        assertEquals(4_000, sw.elapsed(4_000));
    }

    @Test
    public void lapsAreRecordedInOrder() {
        Stopwatch sw = new Stopwatch();
        sw.start(0);
        sw.lap(1_200);
        sw.lap(4_000);
        assertEquals(2, sw.getLaps().size());
        assertEquals(Long.valueOf(4_000), sw.getLaps().get(1));
    }

    @Test
    public void nextTickLandsOnTheNextWholeSecond() {
        Stopwatch sw = new Stopwatch();
        sw.start(0);
        assertEquals(1_000, sw.delayToNextSecond(0));
        assertEquals(700, sw.delayToNextSecond(2_300));
    }

    @Test
    public void snapshotAndRestoreKeepEverything() {
        Stopwatch sw = new Stopwatch();
        sw.start(100);
        sw.lap(600);
        long[] saved = sw.snapshot();
        Stopwatch copy = new Stopwatch();
        copy.restore(saved);
        assertTrue(copy.isRunning());
        assertEquals(sw.elapsed(5_000), copy.elapsed(5_000));
        assertArrayEquals(saved, copy.snapshot());
    }

    @Test
    public void formatsHoursMinutesSeconds() {
        assertEquals("00:00:00", TimeFormat.hms(0));
        assertEquals("00:01:05", TimeFormat.hms(65_999));
        assertEquals("01:00:00", TimeFormat.hms(3_600_000));
        assertEquals("00:00:00", TimeFormat.hms(-50));
    }

    @Test
    public void ringProgressWrapsEveryMinute() {
        assertEquals(0.5f, TimeFormat.minuteProgress(30_000), 0.0001f);
        assertEquals(0f, TimeFormat.minuteProgress(60_000), 0.0001f);
    }

    @Test
    public void splitShowsDifferenceBetweenLaps() {
        assertEquals("+00:00:12", TimeFormat.split(20_000, 8_000));
    }
}
