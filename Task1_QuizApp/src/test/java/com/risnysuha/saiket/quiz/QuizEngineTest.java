package com.risnysuha.saiket.quiz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class QuizEngineTest {

    private QuizEngine engine;

    @Before
    public void setUp() {
        engine = new QuizEngine(Arrays.asList(
                new QuizQuestion("Capital of France?", new String[]{"Paris", "London", "Berlin"}, 0, ""),
                new QuizQuestion("2 + 2?", new String[]{"3", "4"}, 1, "")));
    }

    @Test
    public void firstQuestionFromTheBriefIsParis() {
        QuizQuestion first = QuizBank.questions().get(0);
        assertEquals("What is the capital of France?", first.getText());
        assertEquals(Arrays.asList("Paris", "London", "Berlin"), first.getOptions());
        assertEquals("Paris", first.getCorrectAnswer());
    }

    @Test
    public void correctAnswerAddsToScore() {
        assertTrue(engine.submit(0));
        assertTrue(engine.isAnswered());
        assertTrue(engine.wasCorrect());
        assertEquals(1, engine.getScore());
    }

    @Test
    public void wrongAnswerDoesNotAddToScore() {
        assertFalse(engine.submit(2));
        assertFalse(engine.wasCorrect());
        assertEquals(0, engine.getScore());
        assertEquals(2, engine.getSelectedIndex());
    }

    @Test(expected = IllegalStateException.class)
    public void cannotAnswerTheSameQuestionTwice() {
        engine.submit(0);
        engine.submit(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void submittingWithoutAChoiceIsRejected() {
        engine.submit(QuizEngine.NO_SELECTION);
    }

    @Test(expected = IllegalStateException.class)
    public void cannotSkipAQuestion() {
        engine.next();
    }

    @Test
    public void quizFinishesAfterTheLastQuestion() {
        engine.submit(0);
        engine.next();
        assertEquals(2, engine.getCurrentNumber());
        assertTrue(engine.isLastQuestion());
        engine.submit(1);
        engine.next();
        assertTrue(engine.isFinished());
        assertEquals(2, engine.getScore());
        assertEquals(100, engine.getProgressPercent());
    }

    @Test
    public void restartResetsEverything() {
        engine.submit(0);
        engine.next();
        engine.restart();
        assertEquals(1, engine.getCurrentNumber());
        assertEquals(0, engine.getScore());
        assertFalse(engine.isAnswered());
        assertFalse(engine.isFinished());
    }

    @Test
    public void stateSurvivesSnapshotAndRestore() {
        engine.submit(0);
        int[] saved = engine.snapshot();
        QuizEngine copy = new QuizEngine(Arrays.asList(
                new QuizQuestion("Capital of France?", new String[]{"Paris", "London", "Berlin"}, 0, ""),
                new QuizQuestion("2 + 2?", new String[]{"3", "4"}, 1, "")));
        copy.restore(saved);
        assertTrue(copy.isAnswered());
        assertEquals(1, copy.getScore());
        assertEquals(0, copy.getSelectedIndex());
    }

    @Test
    public void progressCountsAnsweredQuestions() {
        assertEquals(0, engine.getProgressPercent());
        engine.submit(1);
        assertEquals(50, engine.getProgressPercent());
    }

    @Test
    public void verdictMatchesScore() {
        assertTrue(QuizEngine.verdict(5, 5).startsWith("Perfect"));
        assertTrue(QuizEngine.verdict(3, 5).startsWith("Great"));
        assertTrue(QuizEngine.verdict(1, 5).startsWith("Good"));
        assertTrue(QuizEngine.verdict(0, 5).startsWith("No worries"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void questionNeedsAValidCorrectIndex() {
        new QuizQuestion("Bad", new String[]{"A", "B"}, 5, "");
    }
}
