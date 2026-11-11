@echo off
REM run.bat - switch console to UTF-8 and start twdiw_tester.exe
REM usage: run.bat [config file] [--report-full]
cd /d "%~dp0"
chcp 65001 >nul
if not exist twdiw_tester.exe (
    echo twdiw_tester.exe not found. Run build.bat first.
    exit /b 1
)
"%~dp0twdiw_tester.exe" %*
