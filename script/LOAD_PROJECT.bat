@ECHO OFF



SET REPOSITORY=https://gitlab.com/mmg-app/myscanner.git



FOR /f "tokens=4 delims=/" %%a IN ("%REPOSITORY%") do SET PROJECT_NAME=%%a
FOR /f "tokens=1 delims=." %%a IN ("%PROJECT_NAME%") do SET PROJECT_NAME=%%a

CALL git clone -b dev %REPOSITORY%

CD %PROJECT_NAME%
CALL git checkout -b %PROJECT_NAME%

CALL git remote add BuildJob https://gitlab.com/cicd410/building-artifact.git

CD ..
DEL /F "%~f0"
EXIT