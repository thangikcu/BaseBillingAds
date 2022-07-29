@ECHO OFF

CALL git clone -b dev https://gitlab.com/mmg-app/ipviewercpu.git

@REM Sửa tên này = XXX trong URL https://gitlab.com/.../[XXX].git
CD ipviewercpu

CALL git remote add BuildJob https://gitlab.com/cicd210/building-artifacts.git
EXIT