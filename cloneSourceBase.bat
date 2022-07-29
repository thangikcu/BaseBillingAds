@ECHO OFF

CALL git clone -b dev https://gitlab.com/mmg-app/speakinwriting.git

@REM Sửa tên này = XXX trong URL https://gitlab.com/.../[XXX].git
CD speakinwriting

CALL git remote add BuildJob https://gitlab.com/cicd210/building-artifacts.git
EXIT