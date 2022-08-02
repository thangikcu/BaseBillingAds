@ECHO OFF
CALL git clone --single-branch -b ModuleBillingAds https://github.com/thangikcu/BaseBillingAds.git

IF NOT EXIST "app\src\main\assets" MD app\src\main\assets
IF NOT EXIST "app\POST" MD app\POST
ECHO. >> app\POST\Permissions.txt

XCOPY /Y AppstoreAuthenticationKey.pem app\POST
MOVE /Y AppstoreAuthenticationKey.pem app\src\main\assets

MOVE /Y POST\* app\POST
RMDIR /S/Q POST

XCOPY /Y/S res app\src\main\res
RMDIR /S/Q res

CD BaseBillingAds
DEL /F/S/Q .git > NUL
RMDIR /S/Q .git

CD ..
CALL git add BaseBillingAds\*

ECHO #android.useAndroidX=true>> gradle.properties
ECHO #android.enableJetifier=true>> gradle.properties
ECHO org.gradle.parallel=true>> gradle.properties

TYPE settings.gradle >> settings.gradle.temp
DEL -F settings.gradle
ECHO pluginManagement { repositories { gradlePluginPortal(); google(); mavenCentral(); jcenter() } }>> settings.gradle
ECHO plugins { id 'com.android.application' version '7.2.1' apply false; id 'com.android.library' version '7.2.1' apply false; id 'org.jetbrains.kotlin.android' version '1.6.10' apply false }>> settings.gradle
ECHO dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral(); jcenter(); maven { url "https://jitpack.io" } } }>> settings.gradle
TYPE settings.gradle.temp >> settings.gradle
DEL -F settings.gradle.temp
ECHO. >> settings.gradle
ECHO //include ':BaseBillingAds'>> settings.gradle

CD gradle\wrapper
DEL -F gradle-wrapper.properties
ECHO distributionBase=GRADLE_USER_HOME>> gradle-wrapper.properties
ECHO distributionUrl=https\://services.gradle.org/distributions/gradle-7.5-bin.zip>> gradle-wrapper.properties
ECHO distributionPath=wrapper/dists>> gradle-wrapper.properties
ECHO zipStorePath=wrapper/dists>> gradle-wrapper.properties
ECHO zipStoreBase=GRADLE_USER_HOME>> gradle-wrapper.properties
CD ..\..

CD app
Echo. >> build.gradle
ECHO //dependencies { implementation project(path: ':BaseBillingAds') }>> build.gradle
ECHO def APPLICATION_ID>> build.gradle
ECHO def GOOGLE_APPLICATION_ID>> build.gradle
ECHO def AMAZON_APPLICATION_ID>> build.gradle
ECHO def ARCHIVES_BASE_NAME>> build.gradle
ECHO def STORE_FILE>> build.gradle
ECHO def KEY_ALIAS>> build.gradle
ECHO def KEY_PASSWORD>> build.gradle
ECHO def STORE_PASSWORD>> build.gradle
ECHO project.afterEvaluate { preBuild.dependsOn { generatePostData; generateKeystore } }>> build.gradle
ECHO import org.apache.tools.ant.taskdefs.condition.Os>> build.gradle
ECHO task generatePostData() { APPLICATION_ID = android.defaultConfig.applicationId; GOOGLE_APPLICATION_ID = APPLICATION_ID; ARCHIVES_BASE_NAME = "Google_" + APPLICATION_ID; def split = APPLICATION_ID.split("\\."); AMAZON_APPLICATION_ID = APPLICATION_ID.replace(split[1], split[1].md5().replaceAll("[\\d.]", "")); if (gradle.startParameter.taskNames.find { it.toLowerCase().endsWith("amazon") } != null) { APPLICATION_ID = AMAZON_APPLICATION_ID; ARCHIVES_BASE_NAME = "Amazon_" + APPLICATION_ID; }; KEY_ALIAS = "als_${APPLICATION_ID}_keystore"; KEY_PASSWORD = "${APPLICATION_ID}_keystore"; STORE_FILE = "keystore/${ARCHIVES_BASE_NAME}_keystore.jks"; STORE_PASSWORD = "${APPLICATION_ID}_keystore"; file("POST").mkdir(); file("POST/Permissions.txt").createNewFile(); def configFilePath = "POST/BillingInfo.txt"; def configFile = file(configFilePath); configFile.createNewFile(); if (Os.isFamily(Os.FAMILY_WINDOWS)) { configFile.delete(); configFile.createNewFile(); def properties = loadProperties("BaseBillingAds/env/product.properties"); def refundMoney = properties['GOOGLE_REFUND_MONEY']; refundMoney = refundMoney.replace("{", "["); refundMoney = refundMoney.replace("}", "]"); refundMoney = Eval.me(refundMoney); addLineToFile("Google billing", configFilePath); addLineToFile("INTERSTITIAL: ${GOOGLE_APPLICATION_ID}.inapp.nonconsum.rminitial", configFilePath); addLineToFile("BANNER: ${GOOGLE_APPLICATION_ID}.inapp.nonconsum.rmbanner", configFilePath); addLineToFile("CONSUME1: ${GOOGLE_APPLICATION_ID}.inapp.consume.item1" + " REFUND: ${refundMoney[0]}", configFilePath); addLineToFile("CONSUME2: ${GOOGLE_APPLICATION_ID}.inapp.consume.item2" + " REFUND: ${refundMoney[1]}", configFilePath); addLineToFile("CONSUME3: ${GOOGLE_APPLICATION_ID}.inapp.consume.item3" + " REFUND: ${refundMoney[2]}", configFilePath); addLineToFile("NONE_CONSUME1: ${GOOGLE_APPLICATION_ID}.subs.nonconsum.item1" + " REFUND: ${refundMoney[3]}", configFilePath); refundMoney = properties['AMAZON_REFUND_MONEY']; refundMoney = refundMoney.replace("{", "["); refundMoney = refundMoney.replace("}", "]"); refundMoney = Eval.me(refundMoney); addLineToFile("===================================================================================", configFilePath); addLineToFile("===================================================================================", configFilePath); addLineToFile("Amazon billing", configFilePath); addLineToFile("INTERSTITIAL: ${AMAZON_APPLICATION_ID}.amziap.subs.rminitial", configFilePath); addLineToFile("BANNER: ${AMAZON_APPLICATION_ID}.amziap.subs.rmbanner", configFilePath); addLineToFile("CONSUME1: ${AMAZON_APPLICATION_ID}.amziap.consum.buygold1" + " REFUND: ${refundMoney[0]}", configFilePath); addLineToFile("CONSUME2: ${AMAZON_APPLICATION_ID}.amziap.consum.buygold1" + " REFUND: ${refundMoney[1]}", configFilePath); addLineToFile("CONSUME3: ${AMAZON_APPLICATION_ID}.amziap.consum.buygold1" + " REFUND: ${refundMoney[2]}", configFilePath); addLineToFile("ENTITLE_DISCOUNT1: ${AMAZON_APPLICATION_ID}.amziap.entitle.buygold.discount1" + " REFUND: ${refundMoney[3]}", configFilePath) }; project.exec { commandLine("git", "add", "POST/*") } }>> build.gradle
ECHO task generateKeystore() { def store_file = "keystore/Google_${GOOGLE_APPLICATION_ID}_keystore.jks"; def key_alias = "als_${GOOGLE_APPLICATION_ID}_keystore"; def key_password = "${GOOGLE_APPLICATION_ID}_keystore"; def store_password = "${GOOGLE_APPLICATION_ID}_keystore"; def keyStoreFolder = file('keystore'); keyStoreFolder.mkdir(); if (!file(store_file).exists()) { def listFiles = keyStoreFolder.listFiles(); if (listFiles != null) listFiles.each { if (it.getName().startsWith("Google")) it.delete() }; project.exec { executable = 'keytool'; def args = Arrays.asList('-genkey', '-v', '-keystore', store_file, '-alias', key_alias, '-storepass', store_password, '-keypass', key_password, '-dname', "CN=$GOOGLE_APPLICATION_ID", '-keyalg', 'RSA', '-keysize', '2048', '-validity', '10000'); setArgs(args) }; }; store_file = "keystore/Amazon_${AMAZON_APPLICATION_ID}_keystore.jks"; key_alias = "als_${AMAZON_APPLICATION_ID}_keystore"; key_password = "${AMAZON_APPLICATION_ID}_keystore"; store_password = "${AMAZON_APPLICATION_ID}_keystore"; if (!file(store_file).exists()) { def listFiles = keyStoreFolder.listFiles(); if (listFiles != null) listFiles.each { if (it.getName().startsWith("Amazon")) it.delete() }; project.exec { executable = 'keytool'; def args = Arrays.asList('-genkey', '-v', '-keystore', store_file, '-alias', key_alias, '-storepass', store_password, '-keypass', key_password, '-dname', "CN=$AMAZON_APPLICATION_ID", '-keyalg', 'RSA', '-keysize', '2048', '-validity', '10000'); setArgs(args) }; }; project.exec { commandLine 'git', 'add', STORE_FILE } }>> build.gradle
ECHO def addLineToFile(content, path) { if (Os.isFamily(Os.FAMILY_WINDOWS)) project.exec { executable("cmd"); setArgs(Arrays.asList("/C", "echo $content >> $path")) } }>> build.gradle
ECHO def loadProperties(filename) { def properties = new Properties(); rootProject.file(filename).withInputStream { properties.load(it) }; return properties }>> build.gradle
ECHO android { lintOptions { quiet true; abortOnError false; checkReleaseBuilds false; ignoreWarnings false; checkAllWarnings false; warningsAsErrors false; disable 'TypographyFractions','TypographyQuotes'; showAll false; explainIssues false; textReport false; xmlReport false; htmlReport false; sarifReport false; ignore 'TypographyQuotes'; informational 'StopShip'; checkTestSources false; ignoreTestSources true; checkGeneratedSources false; checkDependencies false } }>> build.gradle
ECHO android { signingConfigs { production { keyAlias KEY_ALIAS; keyPassword KEY_PASSWORD; storeFile file(STORE_FILE); storePassword STORE_PASSWORD } }; defaultConfig { applicationId APPLICATION_ID; archivesBaseName = ARCHIVES_BASE_NAME; targetSdk 32; minSdk 21 }; compileSdk 32; buildToolsVersion = "32.0.0"; buildTypes { release { minifyEnabled false; shrinkResources false; signingConfig signingConfigs.production }; roboTest { initWith release }; google { initWith release }; amazon { initWith release } }; variantFilter { if (it.buildType.name == "release") setIgnore(true) }>> build.gradle

@REM CD src\main
@REM ECHO. >> AndroidManifest.xml
@REM ECHO "<!--Bế em vào lòng application đi anh <3 <activity android:parentActivityName="com.wekkkkk.remindernote.LoginActivity" android:name="com.mmgsoft.modules.libs.EntryActivity" android:exported="true" android:screenOrientation="portrait" tools:ignore="LockedOrientationActivity" android:theme="@style/Theme.App.Fullscreen"><intent-filter><action android:name="android.intent.action.MAIN" /><category android:name="android.intent.category.LAUNCHER" /></intent-filter></activity>-->">> AndroidManifest.xml
@REM ECHO. >> AndroidManifest.xml
CD ..
CALL git add .
EXIT