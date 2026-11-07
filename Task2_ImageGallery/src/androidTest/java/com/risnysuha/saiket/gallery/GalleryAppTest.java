package com.risnysuha.saiket.gallery;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Checks the GridView, taps a picture and moves through the viewer. */
@RunWith(AndroidJUnit4.class)
public class GalleryAppTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void browseTheGallery() {
        onView(withId(R.id.gallery_count)).check(matches(withText("12 images")));
        Shots.take("task2_1_grid");

        onData(anything()).inAdapterView(withId(R.id.gallery_grid)).atPosition(5).perform(click());
        onView(withId(R.id.viewer_title)).check(matches(withText("Tea Country")));
        onView(withId(R.id.viewer_counter)).check(matches(withText("6 / 12")));
        Shots.take("task2_2_viewer");

        onView(withId(R.id.next_button)).perform(click());
        onView(withId(R.id.viewer_title)).check(matches(withText("Coral Bay")));
        Shots.take("task2_3_next");

        pressBack();
        Shots.pause(600);
        onData(anything()).inAdapterView(withId(R.id.gallery_grid)).atPosition(11).perform(click());
        onView(withId(R.id.viewer_title)).check(matches(withText("Lagoon Night")));
        onView(withId(R.id.next_button)).perform(click()); // wraps to the first picture
        onView(withId(R.id.viewer_counter)).check(matches(withText("1 / 12")));
        Shots.pause(800);
    }
}
