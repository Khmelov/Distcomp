@echo off
cd /d D:\БГУИР\3 курс\Зимняя\СП\Олеся\Task310\publick
echo Starting compilation...
"C:\Users\Alllex\.m2\wrapper\dists\apache-maven-3.9.12-bin\5nmfsn99br87k5d4ajlekdq10k\apache-maven-3.9.12\bin\mvn.cmd" clean compile
if %errorlevel% neq 0 (
    echo Compilation failed with error code %errorlevel%
) else (
    echo Compilation successful!
)
pause