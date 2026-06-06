package com.quizapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the fixed set of questions used by the quiz.
 */
public final class QuestionBank {
    private QuestionBank() {
    }

    public static List<Question> createQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new Question(
                "Which keyword is used to inherit a class in Java?",
                new String[] {"extends", "implements", "inherits", "super"},
                0));
        questions.add(new Question(
                "Which method is the entry point of a standard Java application?",
                new String[] {"start()", "main()", "run()", "init()"},
                1));
        questions.add(new Question(
                "Which concept means wrapping data and methods together in one unit?",
                new String[] {"Inheritance", "Polymorphism", "Encapsulation", "Abstraction"},
                2));
        questions.add(new Question(
                "Which collection does not allow duplicate elements?",
                new String[] {"ArrayList", "LinkedList", "Set", "Queue"},
                2));
        questions.add(new Question(
                "Which access modifier allows visibility only inside the same class?",
                new String[] {"private", "protected", "public", "default"},
                0));
        questions.add(new Question(
                "What is the default value of a boolean instance variable in Java?",
                new String[] {"true", "false", "0", "null"},
                1));
        questions.add(new Question(
                "Which exception is thrown when dividing an integer by zero?",
                new String[] {"NullPointerException", "ArithmeticException", "IOException", "ClassCastException"},
                1));
        questions.add(new Question(
                "Which keyword prevents a class from being inherited?",
                new String[] {"static", "final", "sealed", "private"},
                1));
        questions.add(new Question(
                "Which Java package contains Swing UI classes such as JFrame?",
                new String[] {"java.io", "java.util", "javax.swing", "java.net"},
                2));
        questions.add(new Question(
                "Which operator is used to compare object references?",
                new String[] {"=", "==", "equals", "compareTo"},
                1));
        questions.add(new Question(
                "Which OOP concept allows the same method name to behave differently?",
                new String[] {"Polymorphism", "Compilation", "Serialization", "Iteration"},
                0));
        questions.add(new Question(
                "Which keyword is used to create an object?",
                new String[] {"class", "new", "this", "void"},
                1));
        questions.add(new Question(
                "Which loop is best when the number of iterations is known?",
                new String[] {"while", "do-while", "for", "switch"},
                2));
        questions.add(new Question(
                "Which interface is commonly implemented to sort custom objects?",
                new String[] {"Runnable", "Serializable", "Comparable", "Cloneable"},
                2));
        questions.add(new Question(
                "Which statement is true about constructors?",
                new String[] {
                    "They must have a return type",
                    "They initialize new objects",
                    "They are inherited automatically",
                    "They must be static"
                },
                1));

        return questions;
    }
}
