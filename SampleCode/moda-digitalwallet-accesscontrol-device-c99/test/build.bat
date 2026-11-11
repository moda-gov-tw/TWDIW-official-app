@echo off
REM build.bat - build twdiw_tester.exe (tries gcc, clang, then MSVC cl)
setlocal
cd /d "%~dp0"

set SRCS=..\src\lib\sha256.c ..\src\lib\hmac_sha256.c ..\src\lib\base64.c ..\src\lib\chacha20_poly1305.c ..\src\lib\x25519.c ..\src\lib\totp.c ..\src\lib\der_x25519.c ..\src\lib\ecc_service.c ..\src\lib\qr_parse.c ..\src\twdiw_platform.c ..\src\twdiw_verify.c twdiw_tester.c
set OUT=twdiw_tester.exe

where gcc >nul 2>nul
if not errorlevel 1 goto use_gcc
where clang >nul 2>nul
if not errorlevel 1 goto use_clang
where cl >nul 2>nul
if not errorlevel 1 goto use_cl

REM cl not on PATH: locate Visual Studio via vswhere and load vcvars64.bat
set "VSWHERE=%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe"
if not exist "%VSWHERE%" goto no_compiler
set "VSPATH="
for /f "usebackq tokens=*" %%i in (`"%VSWHERE%" -latest -products * -requires Microsoft.VisualStudio.Component.VC.Tools.x86.x64 -property installationPath`) do set "VSPATH=%%i"
if not defined VSPATH goto no_compiler
if not exist "%VSPATH%\VC\Auxiliary\Build\vcvars64.bat" goto no_compiler
echo [build] Loading MSVC environment from "%VSPATH%"
call "%VSPATH%\VC\Auxiliary\Build\vcvars64.bat" >nul 2>nul
cd /d "%~dp0"
where cl >nul 2>nul
if not errorlevel 1 goto use_cl

:no_compiler
echo [build] No C compiler found (gcc / clang / cl).
echo         Install one of:
echo           - MinGW-w64 : winget install BrechtSanders.WinLibs.POSIX.UCRT
echo           - LLVM      : winget install LLVM.LLVM
echo           - MSVC      : Visual Studio Build Tools, then run this script
echo                         from "Developer Command Prompt for VS"
exit /b 1

:use_gcc
echo [build] Using gcc
gcc -std=c99 -O2 -Wall -D__USE_MINGW_ANSI_STDIO=1 %SRCS% -o %OUT%
goto check

:use_clang
echo [build] Using clang
clang -std=c99 -O2 -Wall -D_CRT_SECURE_NO_WARNINGS %SRCS% -o %OUT%
goto check

:use_cl
echo [build] Using MSVC cl
if not exist build mkdir build
REM /wd4819: false positive from printf format checking on CP950 systems (source is UTF-8)
cl /nologo /std:c11 /O2 /W3 /utf-8 /wd4819 /D_CRT_SECURE_NO_WARNINGS %SRCS% /Fobuild\ /Fe:%OUT%
goto check

:check
if errorlevel 1 (
    echo [build] FAILED
    exit /b 1
)
echo.
echo [build] OK -^> %OUT%
if not exist tester_config.txt (
    echo Next: copy tester_config.txt.template tester_config.txt
    echo       fill in the keys, then run run.bat
) else (
    echo Next: run run.bat
)
exit /b 0
