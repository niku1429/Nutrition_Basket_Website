package com.quizapp.ui;

import com.quizapp.model.Question;
import com.quizapp.model.Quiz;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

/**
 * Main application window. It owns all screens and connects UI events to quiz actions.
 */
public class QuizAppFrame extends JFrame {
    private static final String HOME_SCREEN = "HOME_SCREEN";
    private static final String QUIZ_SCREEN = "QUIZ_SCREEN";
    private static final String RESULT_SCREEN = "RESULT_SCREEN";

    private final Quiz quiz;
    private final CardLayout cardLayout;
    private final JPanel screens;
    private final JLabel progressLabel;
    private final JLabel questionLabel;
    private final JLabel answeredLabel;
    private final JProgressBar progressBar;
    private final JRadioButton[] optionButtons;
    private final ButtonGroup optionGroup;
    private final JButton previousButton;
    private final JButton nextButton;
    private final JButton submitButton;
    private final JLabel scoreValueLabel;
    private final JLabel correctValueLabel;
    private final JLabel wrongValueLabel;
    private final JLabel percentageValueLabel;

    public QuizAppFrame(Quiz quiz) {
        this.quiz = quiz;
        this.cardLayout = new CardLayout();
        this.screens = new JPanel(cardLayout);
        this.progressLabel = new JLabel();
        this.questionLabel = new JLabel();
        this.answeredLabel = new JLabel();
        this.progressBar = new JProgressBar(0, quiz.getQuestionCount());
        this.optionButtons = new JRadioButton[4];
        this.optionGroup = new ButtonGroup();
        this.previousButton = createSecondaryButton("Previous");
        this.nextButton = createPrimaryButton("Next");
        this.submitButton = createPrimaryButton("Submit");
        this.scoreValueLabel = createResultValueLabel();
        this.correctValueLabel = createResultValueLabel();
        this.wrongValueLabel = createResultValueLabel();
        this.percentageValueLabel = createResultValueLabel();

        configureWindow();
        buildScreens();
        showHomeScreen();
    }

    private void configureWindow() {
        setTitle("Quiz Application");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(860, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);
    }

    private void buildScreens() {
        screens.add(createHomeScreen(), HOME_SCREEN);
        screens.add(createQuizScreen(), QUIZ_SCREEN);
        screens.add(createResultScreen(), RESULT_SCREEN);
        add(screens, BorderLayout.CENTER);
        pack();
    }

    private JPanel createHomeScreen() {
        JPanel screen = createBaseScreen();
        JPanel panel = createContentPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = baseConstraints();
        JLabel title = new JLabel("Quiz Application", SwingConstants.CENTER);
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.TEXT);

        JLabel subtitle = new JLabel("Test your Java and OOP knowledge with 15 multiple-choice questions.", SwingConstants.CENTER);
        subtitle.setFont(Theme.SUBTITLE_FONT);
        subtitle.setForeground(Theme.MUTED_TEXT);

        JLabel details = new JLabel("Navigate freely, review answers, and submit when every question is complete.", SwingConstants.CENTER);
        details.setFont(Theme.BODY_FONT);
        details.setForeground(Theme.MUTED_TEXT);

        JButton startButton = createPrimaryButton("Start Quiz");
        startButton.addActionListener(event -> startQuiz());

        JButton exitButton = createSecondaryButton("Exit");
        exitButton.addActionListener(event -> exitApplication());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        actions.setOpaque(false);
        actions.add(startButton);
        actions.add(exitButton);

        panel.add(title, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(12, 24, 6, 24);
        panel.add(subtitle, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 24, 30, 24);
        panel.add(details, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 24, 0, 24);
        panel.add(actions, gbc);

        screen.add(panel, BorderLayout.CENTER);
        return screen;
    }

    private JPanel createQuizScreen() {
        JPanel screen = createBaseScreen();
        JPanel panel = createContentPanel();
        panel.setLayout(new BorderLayout(0, 24));

        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);
        progressLabel.setFont(Theme.BUTTON_FONT);
        progressLabel.setForeground(Theme.PRIMARY_DARK);
        answeredLabel.setFont(Theme.BODY_FONT);
        answeredLabel.setForeground(Theme.MUTED_TEXT);
        answeredLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        progressBar.setStringPainted(false);
        progressBar.setForeground(Theme.PRIMARY);
        progressBar.setBackground(Theme.BORDER);
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        header.add(progressLabel, BorderLayout.WEST);
        header.add(answeredLabel, BorderLayout.EAST);
        header.add(progressBar, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        questionLabel.setFont(Theme.QUESTION_FONT);
        questionLabel.setForeground(Theme.TEXT);
        questionLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        optionsPanel.setOpaque(false);
        for (int i = 0; i < optionButtons.length; i++) {
            JRadioButton optionButton = createOptionButton();
            final int optionIndex = i;
            optionButton.addActionListener(event -> quiz.answerCurrentQuestion(optionIndex));
            optionButtons[i] = optionButton;
            optionGroup.add(optionButton);
            optionsPanel.add(optionButton);
        }

        center.add(questionLabel, BorderLayout.NORTH);
        center.add(optionsPanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);
        previousButton.addActionListener(event -> showPreviousQuestion());
        nextButton.addActionListener(event -> showNextQuestion());
        submitButton.addActionListener(event -> submitQuiz());
        actions.add(previousButton);
        actions.add(nextButton);
        actions.add(submitButton);

        panel.add(header, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        screen.add(panel, BorderLayout.CENTER);
        return screen;
    }

    private JPanel createResultScreen() {
        JPanel screen = createBaseScreen();
        JPanel panel = createContentPanel();
        panel.setLayout(new BorderLayout(0, 28));

        JLabel title = new JLabel("Quiz Results", SwingConstants.CENTER);
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.TEXT);

        JPanel resultGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        resultGrid.setOpaque(false);
        resultGrid.add(createResultCard("Score", scoreValueLabel, Theme.PRIMARY));
        resultGrid.add(createResultCard("Correct", correctValueLabel, Theme.SUCCESS));
        resultGrid.add(createResultCard("Wrong", wrongValueLabel, Theme.DANGER));
        resultGrid.add(createResultCard("Percentage", percentageValueLabel, Theme.WARNING));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        actions.setOpaque(false);
        JButton restartButton = createPrimaryButton("Restart Quiz");
        restartButton.addActionListener(event -> startQuiz());
        JButton exitButton = createSecondaryButton("Exit");
        exitButton.addActionListener(event -> exitApplication());
        actions.add(restartButton);
        actions.add(exitButton);

        panel.add(title, BorderLayout.NORTH);
        panel.add(resultGrid, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        screen.add(panel, BorderLayout.CENTER);
        return screen;
    }

    private JPanel createBaseScreen() {
        JPanel screen = new JPanel(new BorderLayout());
        screen.setBackground(Theme.BACKGROUND);
        screen.setBorder(BorderFactory.createEmptyBorder(36, 44, 36, 44));
        return screen;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(34, 40, 34, 40)));
        return panel;
    }

    private JPanel createResultCard(String label, JLabel valueLabel, java.awt.Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Theme.PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)));

        JLabel titleLabel = new JLabel(label, SwingConstants.CENTER);
        titleLabel.setFont(Theme.BUTTON_FONT);
        titleLabel.setForeground(Theme.MUTED_TEXT);
        valueLabel.setForeground(accentColor);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = createBaseButton(text);
        button.setBackground(Theme.PRIMARY);
        button.setForeground(java.awt.Color.WHITE);
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = createBaseButton(text);
        button.setBackground(Theme.PANEL);
        button.setForeground(Theme.PRIMARY_DARK);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(11, 18, 11, 18)));
        return button;
    }

    private JButton createBaseButton(String text) {
        JButton button = new JButton(text);
        button.setFont(Theme.BUTTON_FONT);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PRIMARY_DARK),
                BorderFactory.createEmptyBorder(11, 18, 11, 18)));
        return button;
    }

    private JRadioButton createOptionButton() {
        JRadioButton optionButton = new JRadioButton();
        optionButton.setFont(Theme.BODY_FONT);
        optionButton.setForeground(Theme.TEXT);
        optionButton.setBackground(Theme.PANEL);
        optionButton.setFocusPainted(false);
        optionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        optionButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        return optionButton;
    }

    private JLabel createResultValueLabel() {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        return label;
    }

    private GridBagConstraints baseConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 24, 0, 24);
        return gbc;
    }

    private void showHomeScreen() {
        cardLayout.show(screens, HOME_SCREEN);
    }

    private void startQuiz() {
        quiz.restart();
        loadCurrentQuestion();
        cardLayout.show(screens, QUIZ_SCREEN);
    }

    private void showPreviousQuestion() {
        if (!quiz.goToPreviousQuestion()) {
            showMessage("You are already on the first question.");
            return;
        }
        loadCurrentQuestion();
    }

    private void showNextQuestion() {
        if (!quiz.isCurrentQuestionAnswered()) {
            showMessage("Please select an answer before moving to the next question.");
            return;
        }
        if (!quiz.goToNextQuestion()) {
            showMessage("You are on the last question. Use Submit to finish the quiz.");
            return;
        }
        loadCurrentQuestion();
    }

    private void submitQuiz() {
        if (!quiz.isCurrentQuestionAnswered()) {
            showMessage("Please select an answer for this question before submitting.");
            return;
        }
        if (!quiz.areAllQuestionsAnswered()) {
            int firstUnanswered = quiz.getFirstUnansweredQuestionIndex();
            quiz.goToQuestion(firstUnanswered);
            loadCurrentQuestion();
            showMessage("Please answer every question before submitting.");
            return;
        }

        updateResultScreen();
        cardLayout.show(screens, RESULT_SCREEN);
    }

    private void loadCurrentQuestion() {
        Question question = quiz.getCurrentQuestion();
        int questionNumber = quiz.getCurrentQuestionIndex() + 1;
        progressLabel.setText("Question " + questionNumber + " of " + quiz.getQuestionCount());
        answeredLabel.setText(quiz.areAllQuestionsAnswered() ? "All answered" : "In progress");
        progressBar.setValue(questionNumber);
        questionLabel.setText("<html><body style='width: 680px'>" + question.getText() + "</body></html>");

        optionGroup.clearSelection();
        String[] options = question.getOptions();
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setText(options[i]);
            optionButtons[i].setSelected(quiz.getSelectedAnswerForCurrentQuestion() == i);
        }

        previousButton.setEnabled(quiz.hasPreviousQuestion());
        nextButton.setEnabled(quiz.hasNextQuestion());
        submitButton.setEnabled(true);
    }

    private void updateResultScreen() {
        int correct = quiz.getCorrectAnswerCount();
        int wrong = quiz.getWrongAnswerCount();
        int total = quiz.getQuestionCount();

        scoreValueLabel.setText(correct + " / " + total);
        correctValueLabel.setText(String.valueOf(correct));
        wrongValueLabel.setText(String.valueOf(wrong));
        percentageValueLabel.setText(String.format("%.2f%%", quiz.getPercentage()));
    }

    private void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Exit Quiz Application",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Quiz Application", JOptionPane.INFORMATION_MESSAGE);
    }
}
