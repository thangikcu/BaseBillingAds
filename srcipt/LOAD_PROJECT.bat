@ECHO OFF

CALL git clone -b dev https://gitlab.com/mmg-app/customnavigation.git

@REM Sửa tên này = XXX trong URL https://gitlab.com/.../[XXX].git
CD customnavigation

CALL git checkout -b amazon
CALL git remote add BuildJob https://gitlab.com/cicd310/building-artifacts.git
EXIT