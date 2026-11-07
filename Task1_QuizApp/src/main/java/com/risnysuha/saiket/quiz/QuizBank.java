package com.risnysuha.saiket.quiz;

import java.util.Arrays;
import java.util.List;

/** The questions shown in the app. Question 1 is the one from the SaiKet task brief. */
public final class QuizBank {

    private QuizBank() {
    }

    public static List<QuizQuestion> questions() {
        return Arrays.asList(
                new QuizQuestion("What is the capital of France?",
                        new String[]{"Paris", "London", "Berlin"}, 0,
                        "Paris has been the capital of France for more than a thousand years."),
                new QuizQuestion("Which Android view lets the user pick only one option from a group?",
                        new String[]{"CheckBox", "RadioButton inside a RadioGroup", "Switch", "SeekBar"}, 1,
                        "A RadioGroup makes its RadioButtons mutually exclusive, so only one can be checked."),
                new QuizQuestion("Which Activity method runs first when a screen is created?",
                        new String[]{"onStart()", "onResume()", "onCreate()", "onPause()"}, 2,
                        "onCreate() runs first. It is where we call setContentView() and find our views."),
                new QuizQuestion("Where do we normally design an Android screen layout?",
                        new String[]{"In an XML file under res/layout", "In AndroidManifest.xml",
                                "In build.gradle", "In strings.xml"}, 0,
                        "Layouts live in res/layout as XML, and the Activity inflates them."),
                new QuizQuestion("Which listener reacts when the user taps a Button?",
                        new String[]{"OnTouchListener only", "OnLongClickListener",
                                "OnScrollListener", "OnClickListener"}, 3,
                        "View.OnClickListener is called on a normal tap, like our Submit button.")
        );
    }
}
