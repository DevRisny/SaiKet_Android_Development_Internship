package com.risnysuha.saiket.timer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Start, laps, pause and reset on a real device. */
@RunWith(AndroidJUnit4.class)
public class TimerAppTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void startLapPauseAndReset() {
        onView(withId(R.id.time_text)).check(matches(withText("00:00:00")));
        onView(withId(R.id.status_chip)).check(matches(withText("Ready")));
        Shots.take("task5_1_ready");

        onView(withId(R.id.start_button)).perform(click());
        onView(withId(R.id.status_chip)).check(matches(withText("Running")));
        Shots.pause(3300);
        onView(withId(R.id.time_text)).check(matches(withText("00:00:03")));
        onView(withId(R.id.lap_button)).perform(click());
        Shots.pause(4200);
        onView(withId(R.id.lap_button)).perform(click());
        Shots.take("task5_2_running");

        onView(withId(R.id.start_button)).perform(click());
        onView(withId(R.id.status_chip)).check(matches(withText("Paused")));
        onView(withId(R.id.laps_card)).check(matches(isDisplayed()));
        Shots.take("task5_3_paused");

        onView(withId(R.id.reset_button)).perform(click());
        onView(withId(R.id.time_text)).check(matches(withText("00:00:00")));
        onView(withId(R.id.status_chip)).check(matches(withText("Ready")));
        Shots.pause(1200);
    }
}
