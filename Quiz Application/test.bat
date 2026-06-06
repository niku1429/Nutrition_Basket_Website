@echo off
setlocal

if not exist out mkdir out
javac -d out src\com\quizapp\Main.java src\com\quizapp\model\*.java src\com\quizapp\ui\*.java test\QuizApplicationTest.java test\com\quizapp\ui\QuizAppFrameSmokeTest.java
if errorlevel 1 (
    echo Build failed.
    exit /b 1
)

java -cp out QuizApplicationTest
if errorlevel 1 exit /b 1

java -cp out com.quizapp.ui.QuizAppFrameSmokeTest
