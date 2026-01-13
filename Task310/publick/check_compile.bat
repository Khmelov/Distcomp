@echo off
cd /d D:\БГУИР\3 курс\Зимняя\СП\Олеся\Task310\publick
echo Checking compilation...
"C:\Users\Alllex\.m2\wrapper\dists\apache-maven-3.9.12-bin\5nmfsn99br87k5d4ajlekdq10k\apache-maven-3.9.12\bin\mvn.cmd" clean compile > compile_output.txt 2>&1
if %errorlevel% equ 0 (
    echo SUCCESS: Compilation successful!
) else (
    echo FAILED: Compilation failed. Check compile_output.txt for details.
    type compile_output.txt
)
pause