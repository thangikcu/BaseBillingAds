@ECHO OFF
call git clone --single-branch -b ModuleBillingAds https://github.com/thangikcu/BaseBillingAds.git

ECHO. >> settings.gradle
ECHO include ':BaseBillingAds'>> settings.gradle

CD app\
ECHO. >> build.gradle
ECHO def APPLICATION_ID = android.defaultConfig.applicationId>> build.gradle
ECHO. >> build.gradle
ECHO def mKeyAlias = "als_${APPLICATION_ID}_keystore_release_product">> build.gradle
ECHO def mKeyPassword = "${APPLICATION_ID}_keystore_release_product">> build.gradle
ECHO def mStoreFile = "keystore/${APPLICATION_ID}_keystore_release_product.jks">> build.gradle
ECHO def mStorePassword = "${APPLICATION_ID}_keystore_release_product">> build.gradle
Echo. >> build.gradle
ECHO task generateKeystore() { if (!file(mStoreFile).exists()) { file('keystore').mkdir(); project.exec { workingDir(projectDir); executable = 'keytool'; def args = Arrays.asList('-genkey', '-v', '-keystore', mStoreFile, '-alias', mKeyAlias, '-storepass', mStorePassword, '-keypass', mKeyPassword, '-dname', "CN=$APPLICATION_ID", '-keyalg', 'RSA', '-keysize', '2048', '-validity', '10000'); setArgs(args) }; project.exec { workingDir(projectDir); commandLine 'git', 'add', mStoreFile } } }>> build.gradle
Echo. >> build.gradle
ECHO project.afterEvaluate { preBuild.dependsOn generateKeystore }>> build.gradle
Echo. >> build.gradle
ECHO android { signingConfigs { production { keyAlias mKeyAlias; keyPassword mKeyPassword; storeFile file(mStoreFile); storePassword mStorePassword } }; defaultConfig { applicationId APPLICATION_ID; archivesBaseName = APPLICATION_ID; signingConfig signingConfigs.production } }>> build.gradle
Echo. >> build.gradle
ECHO dependencies { implementation project(path: ':BaseBillingAds') }>> build.gradle
CD ..
CD BaseBillingAds\
DEL /F/S/Q .git > NUL
RMDIR /S/q .git
EXIT