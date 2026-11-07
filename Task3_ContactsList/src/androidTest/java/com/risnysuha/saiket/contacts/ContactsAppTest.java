package com.risnysuha.saiket.contacts;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Pattern;

/**
 * Runs on an emulator that CI fills with sample contacts. Accepts the READ_CONTACTS
 * dialog like a real user, then checks the ListView and the search.
 */
@RunWith(AndroidJUnit4.class)
public class ContactsAppTest {

    @Test
    public void grantPermissionAndShowContacts() {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            BySelector allow = By.res(Pattern.compile(".*:id/permission_allow_button"));
            UiObject2 button = device.wait(Until.findObject(allow), 8000);
            if (button == null) {
                button = device.wait(Until.findObject(By.text(Pattern.compile("(?i)allow"))), 3000);
            }
            Shots.take("task3_1_permission");
            assertTrue("The READ_CONTACTS dialog should appear", button != null);
            button.click();

            Shots.pause(1500);
            onView(withId(R.id.contacts_list)).check(matches(isDisplayed()));
            onView(withId(R.id.count_label)).check(matches(withText("12 contacts")));
            Shots.take("task3_2_list");

            onView(withId(R.id.search_input)).perform(typeText("fat"));
            closeSoftKeyboard();
            onView(withId(R.id.count_label)).check(matches(withText("1 of 12 contacts")));
            Shots.take("task3_3_search");
        }
    }
}
