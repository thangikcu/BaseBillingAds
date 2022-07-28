@ECHO OFF
call git clone --single-branch -b ModuleBillingAds https://github.com/thangikcu/BaseBillingAds.git

ECHO android.useAndroidX=true>> gradle.properties
ECHO android.enableJetifier=true>> gradle.properties
ECHO org.gradle.parallel=true>> gradle.properties
ECHO org.gradle.jvmargs=-Xmx2048m -XX:+UseParallelGC -Dfile.encoding=UTF-8>> gradle.properties

TYPE settings.gradle >> settings.gradle.temp
DEL -F settings.gradle
ECHO pluginManagement { repositories { gradlePluginPortal(); google(); mavenCentral() } }>> settings.gradle
ECHO plugins { id 'com.android.application' version '7.2.1' apply false; id 'com.android.library' version '7.2.1' apply false; id 'org.jetbrains.kotlin.android' version '1.6.10' apply false }>> settings.gradle
ECHO dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral(); maven { url "https://jitpack.io" } } }>> settings.gradle
TYPE settings.gradle.temp >> settings.gradle
DEL -F settings.gradle.temp
ECHO. >> settings.gradle
ECHO include ':BaseBillingAds'>> settings.gradle

CD gradle/wrapper
DEL -F gradle-wrapper.properties
ECHO distributionBase=GRADLE_USER_HOME>> gradle-wrapper.properties
ECHO distributionUrl=https\://services.gradle.org/distributions/gradle-7.5-bin.zip>> gradle-wrapper.properties
ECHO distributionPath=wrapper/dists>> gradle-wrapper.properties
ECHO zipStorePath=wrapper/dists>> gradle-wrapper.properties
ECHO zipStoreBase=GRADLE_USER_HOME>> gradle-wrapper.properties
CD ../..

CD app\
ECHO. >> build.gradle
ECHO def APPLICATION_ID = android.defaultConfig.applicationId>> build.gradle
ECHO. >> build.gradle
ECHO def mKeyAlias = "als_${APPLICATION_ID}_keystore_release_product">> build.gradle
ECHO def mKeyPassword = "${APPLICATION_ID}_keystore_release_product">> build.gradle
ECHO def mStoreFile = "keystore/${APPLICATION_ID}_keystore_release_product.jks">> build.gradle
ECHO def mStorePassword = "${APPLICATION_ID}_keystore_release_product">> build.gradle
Echo. >> build.gradle
ECHO task generateKeystore() { if (!file(mStoreFile).exists()) { file('keystore').mkdir(); project.exec { executable = 'keytool'; def args = Arrays.asList('-genkey', '-v', '-keystore', mStoreFile, '-alias', mKeyAlias, '-storepass', mStorePassword, '-keypass', mKeyPassword, '-dname', "CN=$APPLICATION_ID", '-keyalg', 'RSA', '-keysize', '2048', '-validity', '10000'); setArgs(args) }; project.exec { commandLine 'git', 'add', mStoreFile } } }>> build.gradle
Echo. >> build.gradle
ECHO project.afterEvaluate { preBuild.dependsOn generateKeystore }>> build.gradle
Echo. >> build.gradle
ECHO android { signingConfigs { production { keyAlias mKeyAlias; keyPassword mKeyPassword; storeFile file(mStoreFile); storePassword mStorePassword } }; defaultConfig { applicationId APPLICATION_ID; archivesBaseName = APPLICATION_ID; signingConfig signingConfigs.production } }>> build.gradle
Echo. >> build.gradle
ECHO dependencies { implementation project(path: ':BaseBillingAds') }>> build.gradle
ECHO. >> build.gradle
ECHO import org.apache.tools.ant.taskdefs.condition.Os>> build.gradle
ECHO. >> build.gradle
Echo project.afterEvaluate { file("POST").mkdir(); if (!Os.isFamily(Os.FAMILY_WINDOWS)) return; def properties = loadProperties("BaseBillingAds/env/product.properties"); def billingType = properties['BILLING_TYPE']; def refundMoney = properties['REFUND_MONEY']; refundMoney = refundMoney.replace("{", "["); refundMoney = refundMoney.replace("}", "]"); refundMoney = Eval.me(refundMoney); def configFilePath; if (billingType == "com.mmgsoft.modules.libs.helpers.BillingType.GOOGLE") configFilePath = "POST/GoogleBilling.txt" else configFilePath = "POST/AmazonBilling.txt"; def configFile = file(configFilePath); configFile.delete(); configFile.createNewFile(); if (billingType == "com.mmgsoft.modules.libs.helpers.BillingType.GOOGLE") { addLineToFile("INTERSTITIAL: ${APPLICATION_ID}.inapp.nonconsum.rminitial", configFilePath); addLineToFile("BANNER: ${APPLICATION_ID}.inapp.nonconsum.rmbanner", configFilePath); addLineToFile("CONSUME1: ${APPLICATION_ID}.inapp.consume.item1" + " REFUND: ${refundMoney[0]}", configFilePath); addLineToFile("CONSUME2: ${APPLICATION_ID}.inapp.consume.item2" + " REFUND: ${refundMoney[1]}", configFilePath); addLineToFile("CONSUME3: ${APPLICATION_ID}.inapp.consume.item3" + " REFUND: ${refundMoney[2]}", configFilePath); addLineToFile("NONE_CONSUME1: ${APPLICATION_ID}.subs.nonconsum.item1" + " REFUND: ${refundMoney[3]}", configFilePath) } else { addLineToFile("INTERSTITIAL: ${APPLICATION_ID}.amziap.subs.rminitial", configFilePath); addLineToFile("BANNER: ${APPLICATION_ID}.amziap.subs.rmbanner", configFilePath); addLineToFile("CONSUME1: ${APPLICATION_ID}.amziap.consum.buygold1" + " REFUND: ${refundMoney[0]}", configFilePath); addLineToFile("CONSUME2: ${APPLICATION_ID}.amziap.consum.buygold1" + " REFUND: ${refundMoney[1]}", configFilePath); addLineToFile("CONSUME3: ${APPLICATION_ID}.amziap.consum.buygold1" + " REFUND: ${refundMoney[2]}", configFilePath); addLineToFile("ENTITLE_DISCOUNT1: ${APPLICATION_ID}.amziap.entitle.buygold.discount1" + " REFUND: ${refundMoney[3]}", configFilePath) }; project.exec { commandLine("git", "add", "POST/*") } }>> build.gradle
ECHO. >> build.gradle
ECHO def addLineToFile(content, path) { project.exec { executable("cmd"); setArgs(Arrays.asList("/C", "echo $content >> $path")) } }>> build.gradle
ECHO. >> build.gradle
ECHO def loadProperties(filename) { def properties = new Properties(); rootProject.file(filename).withInputStream { properties.load(it) }; return properties }>> build.gradle
CD ..
CD BaseBillingAds\
DEL /F/S/Q .git > NUL
RMDIR /S/q .git
EXIT