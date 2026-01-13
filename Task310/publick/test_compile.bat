@echo off
cd /d D:\БГУИР\3 курс\Зимняя\СП\Олеся\Task310\publick
echo Testing compilation...
call mvnw.cmd clean compile
if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
) else (
    echo Compilation failed!
)
pause