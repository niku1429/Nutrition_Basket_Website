@echo off
setlocal

if not exist out mkdir out
javac -d out src\com\quizapp\Main.java src\com\quizapp\model\*.java src\com\quizapp\ui\*.java
if errorlevel 1 (
    echo Build failed.
    exit /b 1
)

java -cp out com.quizapp.Main
