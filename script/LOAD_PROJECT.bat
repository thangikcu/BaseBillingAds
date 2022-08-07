@ECHO OFF




SET REPOSITORY=https://gitlab.com/mmg-app/qrbarcodeandandroidgenerator.git



IF NOT [%1]==[] SET REPOSITORY=%1

FOR /f "tokens=4 delims=/" %%a IN ("%REPOSITORY%") do SET PROJECT_NAME=%%a
FOR /f "tokens=1 delims=." %%a IN ("%PROJECT_NAME%") do SET PROJECT_NAME=%%a

CALL git clone --progress -b dev %REPOSITORY%

IF EXIST "%PROJECT_NAME%" (
    CD %PROJECT_NAME%
    CALL git checkout -b %PROJECT_NAME%
    CALL git remote add BuildJob https://gitlab.com/cicd410/building-artifact.git
    CD ..
    ECHO.
    ECHO All done! auto exit after...
    TIMEOUT /T 5 & DEL /F "LOAD_PROJECT.bat" & EXIT
)

@REM DEL /F "%~f0"
@REM EXIT