package com.quizapp.model;

import java.util.Arrays;

/**
 * Represents one multiple-choice question with exactly four options.
 */
public class Question {
    private static final int OPTION_COUNT = 4;

    private final String text;
    private final String[] options;
    private final int correctOptionIndex;

    public Question(String text, String[] options, int correctOptionIndex) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty.");
        }
        if (options == null || options.length != OPTION_COUNT) {
            throw new IllegalArgumentException("Each question must have exactly four options.");
        }
        for (String option : options) {
            if (option == null || option.trim().isEmpty()) {
                throw new IllegalArgumentException("Question options cannot be empty.");
            }
        }
        if (correctOptionIndex < 0 || correctOptionIndex >= OPTION_COUNT) {
            throw new IllegalArgumentException("Correct option index must be between 0 and 3.");
        }

        this.text = text;
        this.options = Arrays.copyOf(options, options.length);
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getText() {
        return text;
    }

    public String[] getOptions() {
        return Arrays.copyOf(options, options.length);
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public boolean isCorrect(int selectedOptionIndex) {
        return selectedOptionIndex == correctOptionIndex;
    }
}
