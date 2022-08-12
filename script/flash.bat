@ECHO OFF
SET "__NAME=BillingAds"
SET "__VERSION=v1.0.1"

TITLE %__NAME%

IF [%1]==[] GOTO :USAGE

IF /I "%~1" == "--version" ECHO %__VERSION% & GOTO :END

IF /I "%~1" == "--help" GOTO :USAGE & GOTO :END

IF /I "%~1" == "project" (
    IF [%2]==[] ECHO Missing args url .git of repository & GOTO :END

    CALL LOAD_PROJECT.bat %2 & GOTO :END
)

IF /I "%~1" == "module" CALL LOAD_MODULE.bat & GOTO :END

ECHO %1 not provide! & ECHO. & GOTO :USAGE
GOTO :EOF

:USAGE
ECHO Usage: %0 [--version] [--help]
ECHO    ^<command^> [^<args^>]
ECHO.
ECHO There are commands are available:
ECHO    project [https://[...].git]      load project base
ECHO    module                           load module and generate code
GOTO :EOF

:END
EXIT /B
