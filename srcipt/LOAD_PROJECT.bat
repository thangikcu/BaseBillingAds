@ECHO OFF

CALL git clone -b dev https://gitlab.com/mmg-app/recordingdevice.git

@REM Sửa tên này = XXX trong URL https://gitlab.com/.../[XXX].git
CD recordingdevice

CALL git checkout -b amazon
CALL git remote add BuildJob https://gitlab.com/cicd310/building-artifacts.git
EXIT