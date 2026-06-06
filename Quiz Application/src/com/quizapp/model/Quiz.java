package com.quizapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores quiz state and exposes safe methods for navigation, answers, and score calculation.
 */
public class Quiz {
    private final List<Question> questions;
    private final int[] selectedAnswers;
    private int currentQuestionIndex;

    public Quiz(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Quiz must contain at least one question.");
        }
        this.questions = Collections.unmodifiableList(new ArrayList<>(questions));
        this.selectedAnswers = new int[questions.size()];
        restart();
    }

    public Question getCurrentQuestion() {
        return questions.get(currentQuestionIndex);
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public int getQuestionCount() {
        return questions.size();
    }

    public boolean hasPreviousQuestion() {
        return currentQuestionIndex > 0;
    }

    public boolean hasNextQuestion() {
        return currentQuestionIndex < questions.size() - 1;
    }

    public boolean goToPreviousQuestion() {
        if (!hasPreviousQuestion()) {
            return false;
        }
        currentQuestionIndex--;
        return true;
    }

    public boolean goToNextQuestion() {
        if (!hasNextQuestion()) {
            return false;
        }
        currentQuestionIndex++;
        return true;
    }

    public void answerCurrentQuestion(int optionIndex) {
        if (optionIndex < 0 || optionIndex > 3) {
            throw new IllegalArgumentException("Selected answer must be between 0 and 3.");
        }
        selectedAnswers[currentQuestionIndex] = optionIndex;
    }

    public int getSelectedAnswerForCurrentQuestion() {
        return selectedAnswers[currentQuestionIndex];
    }

    public boolean isCurrentQuestionAnswered() {
        return selectedAnswers[currentQuestionIndex] != -1;
    }

    public boolean areAllQuestionsAnswered() {
        for (int selectedAnswer : selectedAnswers) {
            if (selectedAnswer == -1) {
                return false;
            }
        }
        return true;
    }

    public int getFirstUnansweredQuestionIndex() {
        for (int i = 0; i < selectedAnswers.length; i++) {
            if (selectedAnswers[i] == -1) {
                return i;
            }
        }
        return -1;
    }

    public void goToQuestion(int questionIndex) {
        if (questionIndex < 0 || questionIndex >= questions.size()) {
            throw new IllegalArgumentException("Question index is out of range.");
        }
        currentQuestionIndex = questionIndex;
    }

    public int getCorrectAnswerCount() {
        int correct = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (selectedAnswers[i] != -1 && questions.get(i).isCorrect(selectedAnswers[i])) {
                correct++;
            }
        }
        return correct;
    }

    public int getWrongAnswerCount() {
        int wrong = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (selectedAnswers[i] != -1 && !questions.get(i).isCorrect(selectedAnswers[i])) {
                wrong++;
            }
        }
        return wrong;
    }

    public double getPercentage() {
        return (getCorrectAnswerCount() * 100.0) / questions.size();
    }

    public void restart() {
        for (int i = 0; i < selectedAnswers.length; i++) {
            selectedAnswers[i] = -1;
        }
        currentQuestionIndex = 0;
    }
}
