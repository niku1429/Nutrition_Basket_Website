import com.quizapp.model.Question;
import com.quizapp.model.QuestionBank;
import com.quizapp.model.Quiz;

import java.util.List;

/**
 * Lightweight command-line tests for the quiz model.
 */
public class QuizApplicationTest {
    public static void main(String[] args) {
        List<Question> questions = QuestionBank.createQuestions();
        assertCondition(questions.size() >= 15, "Question bank must contain at least 15 questions.");

        for (Question question : questions) {
            assertCondition(question.getOptions().length == 4, "Each question must have four options.");
            assertCondition(question.getCorrectOptionIndex() >= 0 && question.getCorrectOptionIndex() < 4,
                    "Correct option index must be valid.");
        }

        Quiz quiz = new Quiz(questions);
        assertCondition(quiz.getQuestionCount() == questions.size(), "Quiz question count should match bank.");
        assertCondition(!quiz.areAllQuestionsAnswered(), "Fresh quiz should not be complete.");
        assertCondition(!quiz.hasPreviousQuestion(), "First question should not have a previous question.");

        for (int i = 0; i < quiz.getQuestionCount(); i++) {
            quiz.answerCurrentQuestion(questions.get(i).getCorrectOptionIndex());
            if (i < quiz.getQuestionCount() - 1) {
                assertCondition(quiz.goToNextQuestion(), "Next navigation should work before the last question.");
            }
        }

        assertCondition(quiz.areAllQuestionsAnswered(), "All questions should be answered.");
        assertCondition(quiz.getCorrectAnswerCount() == quiz.getQuestionCount(), "All submitted answers should be correct.");
        assertCondition(quiz.getWrongAnswerCount() == 0, "Wrong answer count should be zero.");
        assertCondition(Math.abs(quiz.getPercentage() - 100.0) < 0.001, "Percentage should be 100.");
        assertCondition(!quiz.goToNextQuestion(), "Next navigation should fail on last question.");

        quiz.restart();
        assertCondition(quiz.getCurrentQuestionIndex() == 0, "Restart should return to first question.");
        assertCondition(!quiz.areAllQuestionsAnswered(), "Restart should clear answers.");

        System.out.println("All quiz model tests passed.");
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
