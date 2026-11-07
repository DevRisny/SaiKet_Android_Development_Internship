package com.risnysuha.saiket.quiz;

import java.util.ArrayList;
import java.util.List;

/**
 * Plain-Java quiz logic with no Android code, so it is easy to unit test.
 * It keeps the current question, the score and whether the current answer was submitted.
 */
public final class QuizEngine {

    public static final int NO_SELECTION = -1;

    private final List<QuizQuestion> questions;
    private int currentIndex;
    private int score;
    private int selectedIndex = NO_SELECTION;
    private boolean answered;
    private boolean finished;

    public QuizEngine(List<QuizQuestion> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("The quiz needs at least one question");
        }
        this.questions = new ArrayList<>(questions);
    }

    public QuizQuestion current() {
        return questions.get(currentIndex);
    }

    public int getCurrentNumber() {
        return currentIndex + 1;
    }

    public int getTotal() {
        return questions.size();
    }

    public int getScore() {
        return score;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public boolean isAnswered() {
        return answered;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isLastQuestion() {
        return currentIndex == questions.size() - 1;
    }

    /** Checks the chosen option, locks the question and returns true when it is right. */
    public boolean submit(int optionIndex) {
        if (finished) {
            throw new IllegalStateException("The quiz is already finished");
        }
        if (answered) {
            throw new IllegalStateException("This question was already answered");
        }
        if (optionIndex < 0 || optionIndex >= current().getOptions().size()) {
            throw new IllegalArgumentException("Choose one of the options first");
        }
        selectedIndex = optionIndex;
        answered = true;
        boolean correct = current().isCorrect(optionIndex);
        if (correct) {
            score++;
        }
        return correct;
    }

    public boolean wasCorrect() {
        return answered && current().isCorrect(selectedIndex);
    }

    /** Moves to the next question, or finishes the quiz after the last one. */
    public void next() {
        if (!answered) {
            throw new IllegalStateException("Submit an answer before moving on");
        }
        if (isLastQuestion()) {
            finished = true;
            return;
        }
        currentIndex++;
        selectedIndex = NO_SELECTION;
        answered = false;
    }

    public void restart() {
        currentIndex = 0;
        score = 0;
        selectedIndex = NO_SELECTION;
        answered = false;
        finished = false;
    }

    /** 0 to 100, based on the questions finished so far. */
    public int getProgressPercent() {
        int done = finished ? questions.size() : currentIndex + (answered ? 1 : 0);
        return Math.round(done * 100f / questions.size());
    }

    /** Saves the state so the quiz survives a screen rotation. */
    public int[] snapshot() {
        return new int[]{currentIndex, score, selectedIndex, answered ? 1 : 0, finished ? 1 : 0};
    }

    public void restore(int[] state) {
        if (state == null || state.length != 5 || state[0] < 0 || state[0] >= questions.size()) {
            return;
        }
        currentIndex = state[0];
        score = state[1];
        selectedIndex = state[2];
        answered = state[3] == 1;
        finished = state[4] == 1;
    }

    /** A short message for the final score screen. */
    public static String verdict(int score, int total) {
        float ratio = total == 0 ? 0 : (float) score / total;
        if (ratio == 1f) {
            return "Perfect score! You know your basics.";
        } else if (ratio >= 0.6f) {
            return "Great job! Just a little more practice.";
        } else if (ratio > 0f) {
            return "Good start. Try again to beat your score.";
        }
        return "No worries, every expert started here. Try again!";
    }
}
