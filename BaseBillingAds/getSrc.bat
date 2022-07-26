@ECHO OFF
call git clone --single-branch -b ModuleBillingAds https://github.com/thangikcu/BaseBillingAds.git
ECHO include ':BaseBillingAds'>> settings.gradle
CD app\
ECHO. >> build.gradle
ECHO dependencies { implementation project(path: ':BaseBillingAds') }>> build.gradle
CD ..
CD BaseBillingAds\
DEL /F/S/Q .git > NUL
RMDIR /S/q .git
EXIT