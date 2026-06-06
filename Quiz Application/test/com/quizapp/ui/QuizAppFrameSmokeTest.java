package com.quizapp.ui;

import com.quizapp.model.QuestionBank;
import com.quizapp.model.Quiz;

import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;

/**
 * Smoke-tests the real Swing controls without displaying a long-running app window.
 */
public class QuizAppFrameSmokeTest {
    public static void main(String[] args) throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("UI smoke test skipped because the environment is headless.");
            return;
        }

        SwingUtilities.invokeAndWait(() -> {
            QuizAppFrame frame = new QuizAppFrame(new Quiz(QuestionBank.createQuestions()));
            try {
                clickButton(frame, "Start Quiz");

                for (int i = 0; i < 15; i++) {
                    clickFirstRadioButton(frame);
                    if (i < 14) {
                        clickButton(frame, "Next");
                    }
                }

                clickButton(frame, "Previous");
                clickButton(frame, "Next");
                clickButton(frame, "Submit");
                clickButton(frame, "Restart Quiz");
                System.out.println("Swing UI smoke test passed.");
            } finally {
                frame.dispose();
            }
        });
    }

    private static void clickButton(Container root, String text) {
        JButton button = findButton(root, text);
        if (button == null) {
            throw new AssertionError("Button not found: " + text);
        }
        if (!button.isEnabled()) {
            throw new AssertionError("Button is disabled: " + text);
        }
        button.doClick();
    }

    private static JButton findButton(Container root, String text) {
        for (Component component : root.getComponents()) {
            if (component instanceof JButton button && text.equals(button.getText())) {
                return button;
            }
            if (component instanceof Container container) {
                JButton found = findButton(container, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static void clickFirstRadioButton(Container root) {
        JRadioButton radioButton = findFirstRadioButton(root);
        if (radioButton == null) {
            throw new AssertionError("No answer option radio button found.");
        }
        radioButton.doClick();
    }

    private static JRadioButton findFirstRadioButton(Container root) {
        for (Component component : root.getComponents()) {
            if (component instanceof JRadioButton radioButton) {
                return radioButton;
            }
            if (component instanceof Container container) {
                JRadioButton found = findFirstRadioButton(container);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
