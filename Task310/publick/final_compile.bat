@echo off
cd /d D:\БГУИР\3 курс\Зимняя\СП\Олеся\Task310\publick
echo Final compilation attempt...
"C:\Users\Alllex\.m2\wrapper\dists\apache-maven-3.9.12-bin\5nmfsn99br87k5d4ajlekdq10k\apache-maven-3.9.12\bin\mvn.cmd" clean compile
if %errorlevel% equ 0 (
    echo SUCCESS: Project compiled successfully!
    echo You can now run the application with: mvnw.cmd spring-boot:run
) else (
    echo FAILED: Compilation failed with errors above.
)
pause