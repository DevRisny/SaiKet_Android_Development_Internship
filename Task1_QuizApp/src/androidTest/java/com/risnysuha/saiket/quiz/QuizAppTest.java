package com.risnysuha.saiket.quiz;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Plays the whole quiz on a real device: one right answer, one wrong answer, then the score. */
@RunWith(AndroidJUnit4.class)
public class QuizAppTest {

    private static final int[] OPTIONS = {R.id.option_1, R.id.option_2, R.id.option_3, R.id.option_4};

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    private void answer(int option) {
        onView(withId(OPTIONS[option])).perform(scrollTo(), click());
        Shots.pause(500);
        onView(withId(R.id.primary_button)).perform(click());
    }

    private void next() {
        Shots.pause(1200);
        onView(withId(R.id.primary_button)).perform(click());
    }

    @Test
    public void playTheQuiz() {
        onView(withId(R.id.question_text)).check(matches(withText("What is the capital of France?")));
        onView(withId(R.id.option_1)).check(matches(withText("Paris")));
        Shots.take("task1_1_question");

        onView(withId(R.id.option_1)).perform(scrollTo(), click());
        Shots.take("task1_2_selected");
        onView(withId(R.id.primary_button)).perform(click());
        onView(withId(R.id.result_title)).check(matches(withText("Correct!")));
        onView(withId(R.id.option_2)).check(matches(isNotEnabled())); // options are locked
        Shots.take("task1_3_correct");
        next();

        answer(0); // a wrong answer on question 2
        onView(withId(R.id.result_title)).check(matches(withText("Incorrect")));
        Shots.take("task1_4_incorrect");
        next();

        answer(2);
        next();
        answer(0);
        next();
        answer(3);
        next();

        onView(withId(R.id.final_score)).check(matches(withText("4 / 5")));
        onView(withId(R.id.score_card)).check(matches(isDisplayed()));
        Shots.take("task1_5_score");
    }
}
