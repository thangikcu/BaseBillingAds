@ECHO OFF
CALL git clone --single-branch -b ModuleBillingAds https://github.com/thangikcu/BaseBillingAds.git

SET ROOT_PATH=%CD%

IF NOT EXIST "app\src\main\assets" MD app\src\main\assets
IF NOT EXIST "app\POST" MD app\POST

XCOPY /Y POST\AppstoreAuthenticationKey.pem app\src\main\assets
XCOPY /Y POST\product.properties BaseBillingAds\env
DEL /F/S/Q POST\product.properties

MOVE /Y POST\* app\POST
RMDIR /S/Q POST

XCOPY /Y/S res app\src\main\res
RMDIR /S/Q res

CD BaseBillingAds
DEL /F/S/Q script > NUL
RMDIR /S/Q script
MD script
XCOPY /Y/S .git .git-disable\
ECHO .git-disable>> .gitignore
DEL /F/S/Q .git > NUL
RMDIR /S/Q .git
CD ..

CALL git add BaseBillingAds\*

ECHO org.gradle.parallel=true>> gradle.properties

TYPE settings.gradle >> settings.gradle.temp
DEL /F settings.gradle
ECHO pluginManagement { repositories { gradlePluginPortal(); google(); mavenCentral(); jcenter() } }>> settings.gradle
ECHO plugins { id 'com.android.application' version '7.2.2' apply false; id 'com.android.library' version '7.2.2' apply false; id 'org.jetbrains.kotlin.android' version '1.7.10' apply false }>> settings.gradle
ECHO dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral(); jcenter(); maven { url "https://jitpack.io" } } }>> settings.gradle
TYPE settings.gradle.temp >> settings.gradle
DEL /F settings.gradle.temp
ECHO. >> settings.gradle
ECHO //@@!include ':BaseBillingAds'>> settings.gradle

TYPE build.gradle >> build.gradle.temp
DEL /F build.gradle
ECHO /*>> build.gradle
TYPE build.gradle.temp >> build.gradle
DEL /F build.gradle.temp
ECHO */>> build.gradle

IF NOT EXIST "gradle\wrapper" MD gradle\wrapper
CD gradle\wrapper
DEL /F gradle-wrapper.properties
ECHO distributionBase=GRADLE_USER_HOME>> gradle-wrapper.properties
ECHO distributionUrl=https\://services.gradle.org/distributions/gradle-7.5.1-bin.zip>> gradle-wrapper.properties
ECHO distributionPath=wrapper/dists>> gradle-wrapper.properties
ECHO zipStorePath=wrapper/dists>> gradle-wrapper.properties
ECHO zipStoreBase=GRADLE_USER_HOME>> gradle-wrapper.properties
CD ..\..

CD app
TYPE build.gradle >> build.gradle.temp
DEL /F build.gradle
ECHO taskBeforeBuild()>> build.gradle
TYPE build.gradle.temp >> build.gradle
DEL /F build.gradle.temp
ECHO. >> build.gradle
ECHO //@@!dependencies { implementation project(path: ':BaseBillingAds') }>> build.gradle
ECHO //2@@!configurations.all { resolutionStrategy.eachDependency { if (requested.group == "com.jakewharton") { if (requested.name == "butterknife") useVersion("10.2.3")else if (requested.name == "butterknife-compiler") useVersion("10.2.1"); because("compatibale with androidx") } } }>> build.gradle
ECHO def APPLICATION_ID>> build.gradle
ECHO def GOOGLE_APPLICATION_ID>> build.gradle
ECHO def AMAZON_APPLICATION_ID>> build.gradle
ECHO def ARCHIVES_BASE_NAME>> build.gradle
ECHO def STORE_FILE>> build.gradle
ECHO def KEY_ALIAS>> build.gradle
ECHO def KEY_PASSWORD>> build.gradle
ECHO def STORE_PASSWORD>> build.gradle
ECHO project.afterEvaluate { preBuild.dependsOn { taskBeforeBuild(); generatePostData; generateKeystore }; assembleAmazonDebug.doLast { taskAfterBuild(); getPermissions(ARCHIVES_BASE_NAME) }; }>> build.gradle
ECHO private void taskBeforeBuild() { def manifestFile = file("src/main/AndroidManifest.xml"); def textFromFile = readFromInputStream(manifestFile); if (!textFromFile.contains("android:exported")) {textFromFile = textFromFile.replaceAll("<activity", "<activity android:exported=\"false\""); textFromFile = textFromFile.replace(textFromFile.substring(textFromFile.indexOf("<intent-filter>"), textFromFile.indexOf("</intent-filter>") + "</intent-filter>".length()), ""); def stream = new FileOutputStream(manifestFile); stream.write(textFromFile.getBytes("UTF-8")); stream.close()}; def gradleFile = file("build.gradle"); def shouldWrite = false; textFromFile = readFromInputStream(gradleFile); if (textFromFile.contains(" com" + "pile ")) {textFromFile = textFromFile.replace(" compile ", " implementation "); shouldWrite = true}; if (readFromInputStream(file("../gradle.properties")).contains("useAndroidX=true")) if (textFromFile.contains("//" + "2@@!")) {textFromFile = textFromFile.replace("//2@@!", ""); shouldWrite = true}; if (shouldWrite) {def stream = new FileOutputStream(gradleFile); stream.write(textFromFile.getBytes("UTF-8")); stream.close()}; }>> build.gradle
ECHO private void taskAfterBuild() { def gradleFile = file("build.gradle"); def shouldWrite = false; def textFromFile = readFromInputStream(gradleFile); if (textFromFile.contains("//" + "@@!")) {textFromFile = "@@!\'Build được rồi!!! giờ Refactor/Migrage to AndroidX nhé\'\n\'Đừng nhấn Sync!!!!\'\n\n" + textFromFile; textFromFile = textFromFile.replace("//@@!", ""); shouldWrite = true}; if (shouldWrite) {def stream = new FileOutputStream(gradleFile); stream.write(textFromFile.getBytes("UTF-8")); stream.close()}; gradleFile = file("../settings.gradle"); textFromFile = readFromInputStream(gradleFile); if (textFromFile.contains("//" + "@@!")) {textFromFile = textFromFile.replace("//@@!", ""); def stream = new FileOutputStream(gradleFile); stream.write(textFromFile.getBytes("UTF-8")); stream.close()}; }>> build.gradle
ECHO import org.apache.tools.ant.taskdefs.condition.Os>> build.gradle
ECHO task generatePostData() { APPLICATION_ID = android.defaultConfig.applicationId; GOOGLE_APPLICATION_ID = APPLICATION_ID; ARCHIVES_BASE_NAME = "Google_" + APPLICATION_ID; def split = APPLICATION_ID.split("\\."); AMAZON_APPLICATION_ID = APPLICATION_ID.replace(split[1], split[1].md5().replaceAll("[\\d.]", "")); if (gradle.startParameter.taskNames.find { it.toLowerCase().contains("amazon") } != null) { APPLICATION_ID = AMAZON_APPLICATION_ID; ARCHIVES_BASE_NAME = "Amazon_" + APPLICATION_ID }; KEY_ALIAS = "als_${APPLICATION_ID}_keystore"; KEY_PASSWORD = "${APPLICATION_ID}_keystore"; STORE_FILE = "keystore/${ARCHIVES_BASE_NAME}_keystore.jks"; STORE_PASSWORD = "${APPLICATION_ID}_keystore"; file("POST").mkdir(); if (Os.isFamily(Os.FAMILY_WINDOWS)) { def configFilePath = "POST/BillingInfo.txt"; def configFile = file(configFilePath); configFile.delete(); configFile.createNewFile(); def properties = loadProperties("BaseBillingAds/env/product.properties"); def id = GOOGLE_APPLICATION_ID.md5().substring(0, 19); def refundMoney = properties['GOOGLE_REFUND_MONEY']; refundMoney = refundMoney.replace("{", "["); refundMoney = refundMoney.replace("}", "]"); refundMoney = Eval.me(refundMoney); addLineToFile("Google billing", configFilePath); addLineToFile("APPLICATION_ID: ${GOOGLE_APPLICATION_ID}", configFilePath); addLineToFile("INTERSTITIAL: ${id}.inapp.nonconsum.rminitial", configFilePath); addLineToFile("BANNER: ${id}.inapp.nonconsum.rmbanner", configFilePath); addLineToFile("CONSUME1: ${id}.inapp.consume.item1" + "  REFUND: ${refundMoney[0]}", configFilePath); addLineToFile("CONSUME2: ${id}.inapp.consume.item2" + "  REFUND: ${refundMoney[1]}", configFilePath); addLineToFile("CONSUME3: ${id}.inapp.consume.item3" + "  REFUND: ${refundMoney[2]}", configFilePath); addLineToFile("NONE_CONSUME1: ${id}.subs.nonconsum.item1" + "  REFUND: ${refundMoney[3]}", configFilePath); id = AMAZON_APPLICATION_ID.md5().substring(0, 19); refundMoney = properties['AMAZON_REFUND_MONEY']; refundMoney = refundMoney.replace("{", "["); refundMoney = refundMoney.replace("}", "]"); refundMoney = Eval.me(refundMoney); addLineToFile("======================================================================", configFilePath); addLineToFile("======================================================================", configFilePath); addLineToFile("Amazon billing", configFilePath); addLineToFile("APPLICATION_ID: ${AMAZON_APPLICATION_ID}", configFilePath); addLineToFile("INTERSTITIAL: ${id}.amziap.subs.rminitial", configFilePath); addLineToFile("BANNER: ${id}.amziap.subs.rmbanner", configFilePath); addLineToFile("CONSUME1: ${id}.amziap.consum.buygold1" + "  REFUND: ${refundMoney[0]}", configFilePath); addLineToFile("CONSUME2: ${id}.amziap.consum.buygold2" + "  REFUND: ${refundMoney[1]}", configFilePath); addLineToFile("CONSUME3: ${id}.amziap.consum.buygold3" + "  REFUND: ${refundMoney[2]}", configFilePath); addLineToFile("ENTITLE_DISCOUNT1: ${id}.amziap.entitle.buygold.discount1" + "  REFUND: ${refundMoney[3]}", configFilePath) }; project.exec { commandLine("git", "add", "POST/*") } }>> build.gradle
ECHO task generateKeystore() { def store_file = "keystore/Google_${GOOGLE_APPLICATION_ID}_keystore.jks"; def key_alias = "als_${GOOGLE_APPLICATION_ID}_keystore"; def key_password = "${GOOGLE_APPLICATION_ID}_keystore"; def store_password = "${GOOGLE_APPLICATION_ID}_keystore"; def keyStoreFolder = file('keystore'); keyStoreFolder.mkdir(); if (!file(store_file).exists()) { def listFiles = keyStoreFolder.listFiles(); if (listFiles != null) listFiles.each { if (it.getName().startsWith("Google")) it.delete() }; project.exec { executable = 'keytool'; def args = Arrays.asList('-genkey', '-v', '-keystore', store_file, '-alias', key_alias, '-storepass', store_password, '-keypass', key_password, '-dname', "CN=$GOOGLE_APPLICATION_ID", '-keyalg', 'RSA', '-keysize', '2048', '-validity', '10000'); setArgs(args) }; }; store_file = "keystore/Amazon_${AMAZON_APPLICATION_ID}_keystore.jks"; key_alias = "als_${AMAZON_APPLICATION_ID}_keystore"; key_password = "${AMAZON_APPLICATION_ID}_keystore"; store_password = "${AMAZON_APPLICATION_ID}_keystore"; if (!file(store_file).exists()) { def listFiles = keyStoreFolder.listFiles(); if (listFiles != null) listFiles.each { if (it.getName().startsWith("Amazon")) it.delete() }; project.exec { executable = 'keytool'; def args = Arrays.asList('-genkey', '-v', '-keystore', store_file, '-alias', key_alias, '-storepass', store_password, '-keypass', key_password, '-dname', "CN=$AMAZON_APPLICATION_ID", '-keyalg', 'RSA', '-keysize', '2048', '-validity', '10000'); setArgs(args) }; }; project.exec { commandLine 'git', 'add', 'keystore' } }>> build.gradle
ECHO private void getPermissions(String archivesBaseName) { def apkPath = "build/outputs/apk/amazon/debug/${archivesBaseName}-amazon-debug.apk"; if (file(apkPath).exists()) {def file = file("POST/Permissions.txt"); file.delete(); file.createNewFile(); def byteArrayOutputStream = new ByteArrayOutputStream(); project.exec { setStandardOutput(byteArrayOutputStream); commandLine 'aapt', 'd', 'permissions', apkPath }; def stream = new FileOutputStream(file); stream.write(byteArrayOutputStream.toString().getBytes("UTF-8")); stream.close(); project.exec { commandLine("git", "add", "POST/*") }} }>> build.gradle
ECHO def addLineToFile(content, path) { if (Os.isFamily(Os.FAMILY_WINDOWS)) project.exec { executable("cmd"); setArgs(Arrays.asList("/C", "echo ${content}>> $path")) } }>> build.gradle
ECHO def loadProperties(filename) { def properties = new Properties(); rootProject.file(filename).withInputStream { properties.load(it) }; return properties }>> build.gradle
ECHO private static String readFromInputStream(File file) { StringBuilder resultStringBuilder = new StringBuilder(); BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8")); String line; while ((line = br.readLine()) != null) resultStringBuilder.append(line).append("\n"); br.close(); return resultStringBuilder.toString(); }>> build.gradle
ECHO android { lintOptions { quiet true; abortOnError false; checkReleaseBuilds false; ignoreWarnings false; checkAllWarnings false; warningsAsErrors false; disable 'TypographyFractions','TypographyQuotes'; showAll false; explainIssues false; textReport false; xmlReport false; htmlReport false; sarifReport false; ignore 'TypographyQuotes'; informational 'StopShip'; checkTestSources false; ignoreTestSources true; checkGeneratedSources false; checkDependencies false } }>> build.gradle
ECHO android { signingConfigs { production { keyAlias KEY_ALIAS; keyPassword KEY_PASSWORD; storeFile file(STORE_FILE); storePassword STORE_PASSWORD } }; defaultConfig { applicationId APPLICATION_ID; archivesBaseName = ARCHIVES_BASE_NAME; targetSdk 32; minSdk 21; versionCode 1; versionName "v1.0.0" }; compileSdk 32; buildToolsVersion = "32.0.0"; buildTypes { release { minifyEnabled false; shrinkResources false; signingConfig signingConfigs.production }; }; flavorDimensions "default"; productFlavors { roboTest { versionNameSuffix "-roboTest" }; google { versionNameSuffix "-google" }; amazon { versionNameSuffix "-amazon" } } }>> build.gradle
CD ..

FOR %%I IN (.) DO SET PARENT_FOLDER=%%~nxI
FOR %%I IN (..) DO SET "PARENT_FOLDER=%%~nxI\%PARENT_FOLDER%"
SET PARENT_FOLDER_NEW=%PARENT_FOLDER: =%
IF NOT "%PARENT_FOLDER%" == "%PARENT_FOLDER_NEW%" (
    CD ..\..
    XCOPY /Y/S "%PARENT_FOLDER%" %PARENT_FOLDER_NEW%\
    FOR /D %%a IN ("%PARENT_FOLDER%\*") DO RD /S/Q "%%a"
    FOR %%a IN ("%PARENT_FOLDER%\*") DO IF /I NOT "%%~nxa"=="LOAD_MODULE.bat" DEL /F "%%a"
)
CALL git add -A .
CALL git commit -m "Init project with script"

ECHO.
ECHO All done! auto exit after...
CD %ROOT_PATH%
TIMEOUT /T 5 & DEL /F "LOAD_MODULE.bat" & EXIT

@REM DEL /F "%~f0"
@REM EXIT