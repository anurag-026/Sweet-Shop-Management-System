@echo off
echo Running Sweet Shop Backend Unit Tests...
echo.

echo Compiling and running all tests...
mvn clean test

echo.
echo Test execution completed.
echo Check the target/surefire-reports directory for detailed test results.
pause
