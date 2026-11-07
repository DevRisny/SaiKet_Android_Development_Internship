package com.risnysuha.saiket.activityswitch;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/** ActivityOne -> ActivityTwo with a message, then a reply comes back. */
@RunWith(AndroidJUnit4.class)
public class ActivitySwitchTest {

    private static final String MESSAGE = "Hello from Activity One! Intents carry data.";

    @Rule
    public ActivityScenarioRule<ActivityOne> rule = new ActivityScenarioRule<>(ActivityOne.class);

    @Test
    public void messageTravelsBothWays() {
        Shots.take("task4_1_activity_one");
        onView(withId(R.id.message_input)).perform(typeText(MESSAGE));
        closeSoftKeyboard();
        Shots.take("task4_2_typed");

        onView(withId(R.id.send_button)).perform(click());
        Shots.pause(800);
        onView(withId(R.id.received_message)).check(matches(withText(MESSAGE)));
        onView(withId(R.id.stat_words)).check(matches(withText("7")));
        Shots.take("task4_3_activity_two");

        onView(withId(R.id.reply_input)).perform(scrollTo(), typeText("Got it, thanks Risny!"));
        closeSoftKeyboard();
        Shots.pause(500);
        onView(withId(R.id.reply_button)).perform(scrollTo(), click());
        Shots.pause(900);

        onView(withId(R.id.reply_text)).check(matches(withText("Got it, thanks Risny!")));
        onView(withId(R.id.reply_card)).check(matches(isDisplayed()));
        Shots.take("task4_4_reply");
    }
}
