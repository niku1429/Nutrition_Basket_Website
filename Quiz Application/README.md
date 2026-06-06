# Quiz Application

A complete Java Swing desktop quiz application built with Object-Oriented Programming principles.

## Features

- Home screen with Start Quiz and Exit buttons
- 15 Java/OOP multiple-choice questions
- Four answer options per question
- Previous, Next, and Submit controls
- Input validation before navigation and submission
- Result screen with score, correct answers, wrong answers, and percentage
- Restart Quiz and Exit buttons
- Clean, professional Swing layout with consistent colors and spacing

## Project Structure

```text
Quiz Application/
├── README.md
├── run.bat
├── test.bat
├── src/
│   └── com/
│       └── quizapp/
│           ├── Main.java
│           ├── model/
│           │   ├── Question.java
│           │   ├── QuestionBank.java
│           │   └── Quiz.java
│           └── ui/
│               ├── QuizAppFrame.java
│               └── Theme.java
└── test/
    ├── QuizApplicationTest.java
    └── com/
        └── quizapp/
            └── ui/
                └── QuizAppFrameSmokeTest.java
```

## File Explanation

- `src/com/quizapp/Main.java`: Application entry point. Creates the quiz and opens the Swing window.
- `src/com/quizapp/model/Question.java`: Represents a single MCQ with question text, four options, and the correct answer.
- `src/com/quizapp/model/QuestionBank.java`: Stores the 15 ready-to-use quiz questions.
- `src/com/quizapp/model/Quiz.java`: Handles quiz state, answer saving, navigation, validation, score, wrong count, and percentage.
- `src/com/quizapp/ui/Theme.java`: Stores shared colors and fonts for a consistent UI.
- `src/com/quizapp/ui/QuizAppFrame.java`: Builds the Home, Quiz, and Result screens and wires every button action.
- `test/QuizApplicationTest.java`: Simple command-line tests for question data, navigation, restart, and score calculation.
- `test/com/quizapp/ui/QuizAppFrameSmokeTest.java`: Smoke test that clicks through the main Swing controls.
- `run.bat`: Compiles and launches the application on Windows.
- `test.bat`: Compiles and runs both automated tests on Windows.

## How to Compile

From the `Quiz Application` folder:

```bash
javac -d out src/com/quizapp/Main.java src/com/quizapp/model/*.java src/com/quizapp/ui/*.java
```

On Windows, you can also run:

```bat
run.bat
```

## How to Run

```bash
java -cp out com.quizapp.Main
```

## How to Run Tests

```bash
javac -d out src/com/quizapp/Main.java src/com/quizapp/model/*.java src/com/quizapp/ui/*.java test/QuizApplicationTest.java test/com/quizapp/ui/QuizAppFrameSmokeTest.java
java -cp out QuizApplicationTest
java -cp out com.quizapp.ui.QuizAppFrameSmokeTest
```

On Windows, you can also run:

```bat
test.bat
```

## Step-by-Step Project Flow

1. The application opens on the Home screen.
2. The user clicks `Start Quiz`.
3. The quiz resets all previous answers and loads question 1.
4. The user selects one option.
5. The user clicks `Next` to move forward. If no option is selected, a message is shown.
6. The user can click `Previous` to review or change earlier answers.
7. The user clicks `Submit` after answering all questions.
8. If any question is unanswered, the app jumps to the first unanswered question and shows a message.
9. The Result screen displays score, correct answer count, wrong answer count, and percentage.
10. The user can click `Restart Quiz` to begin again or `Exit` to close the application.
