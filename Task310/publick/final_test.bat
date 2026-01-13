@echo off
cd /d D:\БГУИР\3 курс\Зимняя\СП\Олеся\Task310\publick
echo === FINAL COMPILATION TEST ===
echo.

echo Cleaning project...
"C:\Users\Alllex\.m2\wrapper\dists\apache-maven-3.9.12-bin\5nmfsn99br87k5d4ajlekdq10k\apache-maven-3.9.12\bin\mvn.cmd" clean > clean.log 2>&1
if %errorlevel% neq 0 (
    echo CLEAN FAILED
    type clean.log
    goto :error
)

echo Compiling project...
"C:\Users\Alllex\.m2\wrapper\dists\apache-maven-3.9.12-bin\5nmfsn99br87k5d4ajlekdq10k\apache-maven-3.9.12\bin\mvn.cmd" compile > compile.log 2>&1
if %errorlevel% neq 0 (
    echo COMPILATION FAILED
    type compile.log
    goto :error
)

echo.
echo === SUCCESS! ===
echo Project compiled successfully!
echo You can now run the application with:
echo mvnw.cmd spring-boot:run
echo.
echo Application will be available at: http://localhost:24110/api/v1.0/
echo.
pause
exit /b 0

:error
echo.
echo === COMPILATION FAILED ===
echo Check the logs above for error details.
echo.
pause
exit /b 1