package com.quizapp;

import com.quizapp.model.QuestionBank;
import com.quizapp.model.Quiz;
import com.quizapp.ui.QuizAppFrame;

import javax.swing.SwingUtilities;

/**
 * Starts the Quiz Application.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Quiz quiz = new Quiz(QuestionBank.createQuestions());
            QuizAppFrame frame = new QuizAppFrame(quiz);
            frame.setVisible(true);
        });
    }
}
