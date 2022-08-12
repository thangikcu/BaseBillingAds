@ECHO OFF




SET REPOSITORY=https://gitlab.com/mmg-app/qrbarcodeandandroidgenerator.git



IF NOT [%1]==[] SET REPOSITORY=%1

IF /I "%REPOSITORY%" == "test" SET REPOSITORY=https://gitlab.com/mmg-app/qrbarcodeandandroidgenerator.git

FOR /f "tokens=4 delims=/" %%a IN ("%REPOSITORY%") do SET PROJECT_NAME=%%a
FOR /f "tokens=1 delims=." %%a IN ("%PROJECT_NAME%") do SET PROJECT_NAME=%%a

CALL git clone --progress -b dev %REPOSITORY%

IF EXIST "%PROJECT_NAME%" (
    CD %PROJECT_NAME%
    CALL git checkout -b %PROJECT_NAME%
    CALL git remote add BuildJob https://gitlab.com/cicd410/building-artifact.git
    MD NextStep
    CD NextStep
    MD res
    MD POST
    CD POST
    ECHO ADMOB_APP_ID=>> product.properties
    ECHO BANNER_AD_UNIT_ID=>> product.properties
    ECHO INTERSTITIAL_AD_UNIT_ID=>> product.properties
    ECHO #@@! Code below will be auto generate>> product.properties
    ECHO GOOGLE_REFUND_MONEY={"500", "1000", "2000", "5000"}>> product.properties
    ECHO AMAZON_REFUND_MONEY={"600", "1500", "3000", "7000"}>> product.properties
    CD ..
    CD ..
    CD ..
    ECHO.
    ECHO All done! auto exit after...
    TIMEOUT /T 5
    IF EXIST "LOAD_PROJECT.bat" DEL /F "LOAD_PROJECT.bat"
    EXIT
)

@REM DEL /F "%~f0"
@REM EXIT