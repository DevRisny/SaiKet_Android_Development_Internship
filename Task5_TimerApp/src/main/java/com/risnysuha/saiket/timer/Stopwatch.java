package com.risnysuha.saiket.timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Plain-Java timer state. The current time is passed in (from SystemClock.elapsedRealtime()
 * in the app), so elapsed time stays exact even if a Handler tick arrives a little late,
 * and the class is easy to unit test.
 */
public final class Stopwatch {

    private long accumulatedMs;
    private long startedAt = -1;
    private final List<Long> laps = new ArrayList<>();

    public boolean isRunning() {
        return startedAt >= 0;
    }

    public boolean isReset() {
        return !isRunning() && accumulatedMs == 0 && laps.isEmpty();
    }

    public void start(long now) {
        if (!isRunning()) {
            startedAt = now;
        }
    }

    public void pause(long now) {
        if (isRunning()) {
            accumulatedMs += Math.max(0, now - startedAt);
            startedAt = -1;
        }
    }

    /** Stops the timer and sets the time back to zero. */
    public void reset() {
        accumulatedMs = 0;
        startedAt = -1;
        laps.clear();
    }

    public long elapsed(long now) {
        return accumulatedMs + (isRunning() ? Math.max(0, now - startedAt) : 0);
    }

    /** Records the current time as a lap and returns it. */
    public long lap(long now) {
        long time = elapsed(now);
        laps.add(time);
        return time;
    }

    public List<Long> getLaps() {
        return Collections.unmodifiableList(laps);
    }

    /** Milliseconds until the next whole second, so the Handler ticks right on each second. */
    public long delayToNextSecond(long now) {
        return 1000 - (elapsed(now) % 1000);
    }

    // ----- save and restore across a screen rotation -----

    public long[] snapshot() {
        long[] state = new long[2 + laps.size()];
        state[0] = accumulatedMs;
        state[1] = startedAt;
        for (int i = 0; i < laps.size(); i++) {
            state[2 + i] = laps.get(i);
        }
        return state;
    }

    public void restore(long[] state) {
        if (state == null || state.length < 2) {
            return;
        }
        accumulatedMs = state[0];
        startedAt = state[1];
        laps.clear();
        for (int i = 2; i < state.length; i++) {
            laps.add(state[i]);
        }
    }
}
