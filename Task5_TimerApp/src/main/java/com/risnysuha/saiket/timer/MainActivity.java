package com.risnysuha.saiket.timer;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Task 5 · Simple Timer App.
 * A Handler posts a Runnable that updates the TextView every second with the elapsed time.
 * "Start" begins the timer (and becomes Pause / Resume), "Reset" stops it and sets it back to zero.
 */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_STATE = "stopwatch_state";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Stopwatch stopwatch = new Stopwatch();

    private TextView timeText;
    private TextView statusChip;
    private RingProgressView ring;
    private MaterialButton startButton;
    private MaterialButton resetButton;
    private MaterialButton lapButton;
    private View lapsCard;
    private LinearLayout lapsList;
    private TextView lapsCount;

    /** Runs once a second while the timer is running and refreshes the time on screen. */
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            updateTime();
            if (stopwatch.isRunning()) {
                handler.postDelayed(this, stopwatch.delayToNextSecond(now()));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeInsets.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeInsets.apply(findViewById(R.id.root), findViewById(R.id.header));

        timeText = findViewById(R.id.time_text);
        statusChip = findViewById(R.id.status_chip);
        ring = findViewById(R.id.ring);
        startButton = findViewById(R.id.start_button);
        resetButton = findViewById(R.id.reset_button);
        lapButton = findViewById(R.id.lap_button);
        lapsCard = findViewById(R.id.laps_card);
        lapsList = findViewById(R.id.laps_list);
        lapsCount = findViewById(R.id.laps_count);

        if (savedInstanceState != null) {
            stopwatch.restore(savedInstanceState.getLongArray(KEY_STATE));
        }

        startButton.setOnClickListener(v -> {
            if (stopwatch.isRunning()) {
                stopwatch.pause(now());
                handler.removeCallbacks(ticker);
            } else {
                stopwatch.start(now());
                handler.removeCallbacks(ticker);
                handler.postDelayed(ticker, stopwatch.delayToNextSecond(now()));
            }
            render();
        });

        resetButton.setOnClickListener(v -> {
            handler.removeCallbacks(ticker); // stop the timer
            stopwatch.reset();               // and set the time back to zero
            render();
        });

        lapButton.setOnClickListener(v -> {
            stopwatch.lap(now());
            render();
        });

        render();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (stopwatch.isRunning()) {
            handler.removeCallbacks(ticker);
            handler.post(ticker);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // The time is kept in Stopwatch, so it stays correct while the screen is not visible.
        handler.removeCallbacks(ticker);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLongArray(KEY_STATE, stopwatch.snapshot());
    }

    private static long now() {
        return SystemClock.elapsedRealtime();
    }

    private void updateTime() {
        long elapsed = stopwatch.elapsed(now());
        timeText.setText(TimeFormat.hms(elapsed));
        ring.setProgress(TimeFormat.minuteProgress(elapsed));
    }

    private void render() {
        updateTime();
        boolean running = stopwatch.isRunning();
        boolean reset = stopwatch.isReset();

        startButton.setText(running ? R.string.pause : (reset ? R.string.start : R.string.resume));
        startButton.setIconResource(running ? R.drawable.ic_pause : R.drawable.ic_play);
        resetButton.setEnabled(!reset);
        lapButton.setEnabled(running);

        int statusText = running ? R.string.status_running : (reset ? R.string.status_ready : R.string.status_paused);
        int statusFg = running ? R.color.success : (reset ? R.color.brand_blue_dark : R.color.amber);
        int statusBg = running ? R.color.success_bg : (reset ? R.color.brand_blue_soft : R.color.amber_bg);
        statusChip.setText(statusText);
        statusChip.setTextColor(ContextCompat.getColor(this, statusFg));
        statusChip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, statusBg)));

        renderLaps();
    }

    private void renderLaps() {
        List<Long> laps = stopwatch.getLaps();
        lapsCard.setVisibility(laps.isEmpty() ? View.GONE : View.VISIBLE);
        lapsCount.setText(getResources().getQuantityString(R.plurals.laps_count, laps.size(), laps.size()));
        lapsList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        // Newest lap first.
        for (int i = laps.size() - 1; i >= 0; i--) {
            View row = inflater.inflate(R.layout.item_lap, lapsList, false);
            long previous = i == 0 ? 0 : laps.get(i - 1);
            ((TextView) row.findViewById(R.id.lap_number)).setText(getString(R.string.lap_format, i + 1));
            ((TextView) row.findViewById(R.id.lap_split)).setText(TimeFormat.split(laps.get(i), previous));
            ((TextView) row.findViewById(R.id.lap_time)).setText(TimeFormat.hms(laps.get(i)));
            lapsList.addView(row);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
