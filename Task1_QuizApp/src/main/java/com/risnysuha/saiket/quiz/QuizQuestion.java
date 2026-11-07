package com.risnysuha.saiket.quiz;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** One multiple-choice question: the text, its answer options and the index of the right one. */
public final class QuizQuestion {

    private final String text;
    private final List<String> options;
    private final int correctIndex;
    private final String explanation;

    public QuizQuestion(String text, String[] options, int correctIndex, String explanation) {
        if (options == null || options.length < 2 || options.length > 4) {
            throw new IllegalArgumentException("A question needs 2 to 4 options");
        }
        if (correctIndex < 0 || correctIndex >= options.length) {
            throw new IllegalArgumentException("correctIndex is outside the options");
        }
        this.text = text;
        this.options = Collections.unmodifiableList(Arrays.asList(options.clone()));
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public String getCorrectAnswer() {
        return options.get(correctIndex);
    }

    public String getExplanation() {
        return explanation;
    }

    public boolean isCorrect(int optionIndex) {
        return optionIndex == correctIndex;
    }
}
