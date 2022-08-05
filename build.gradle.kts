import com.android.build.api.dsl.LibraryBuildType
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
}

android {

    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            ext.set("enableCrashlytics", false)
            ext.set("alwaysUpdateBuildId", false)
            loadEnv(this, "env/dev.properties")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            loadEnv(this, "env/product.properties")
        }
    }
    flavorDimensions += "default"
    productFlavors {
        create("google") {
        }

        create("amazon") {

        }

        create("roboTest") {
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }

        singleVariant("debug") {
            withSourcesJar()
        }
    }
}


dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.activity:activity-ktx:1.5.1")
    implementation("com.google.android.material:material:1.6.1")

    implementation("com.google.android.gms:play-services-ads:21.1.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    implementation("com.intuit.sdp:sdp-android:1.0.6")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.android.billingclient:billing-ktx:5.0.0")
    implementation("com.android.billingclient:billing:5.0.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.13.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.annotation:annotation:1.4.0")
//    implementation ("com.amazon.device:amazon-appstore-sdk:3.0.2")


    api("com.amazon.device:amazon-appstore-sdk:3.0.3")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(platform("com.google.firebase:firebase-bom:30.3.1"))

    // UPDATE BUILD GRADLE
    // parser
    // database
    implementation("androidx.room:room-rxjava2:2.4.3")
    annotationProcessor("androidx.room:room-compiler:2.4.3")
}

afterEvaluate {
    generateRefundMoney()
    validate()
}

fun Project.generateRefundMoney() {
    val file = file("env/product.properties")

    var content = file.readText()
    if (content.contains("#@@!")) {
        content = content.substringBefore("#@@!")

        fun generate() = arrayOf(
            (5..12).random(),
            (15..30).random(),
            (35..45).random(),
            (50..120).random(),
        ).joinToString(prefix = "{", postfix = "}") {
            String.format("\"%s00\"", it)
        }

        content += "\nGOOGLE_REFUND_MONEY=${generate()}"
        content += "\nAMAZON_REFUND_MONEY=${generate()}"

        file.writeText(content)
    }
}

fun Project.validate() {
    val manifestFile = file("../app/src/main/AndroidManifest.xml")
    var textFromFile = manifestFile.readText()
    var shouldWrite = false

    if (textFromFile.contains("com.google.android.gms.ads.APPLICATION_ID")) {
        textFromFile = textFromFile.replace("com.google.android.gms.ads.APPLICATION_ID", "null")
        shouldWrite = true
    }

    if (!textFromFile.contains("com.mmgsoft.modules.libs.EntryActivity")) {
        val indexOfActivity = textFromFile.indexOf(
            "android:name=\"",
            textFromFile.indexOf("<activity")
        ) + "android:name=\"".length
        val activity =
            textFromFile.substring(indexOfActivity, textFromFile.indexOf('\"', indexOfActivity))
        textFromFile = textFromFile.replaceFirst(
            "<activity",
            "<activity android:parentActivityName=\"$activity\" xmlns:tools=\"http://schemas.android.com/tools\" android:name=\"com.mmgsoft.modules.libs.EntryActivity\" android:exported=\"true\" android:screenOrientation=\"portrait\" android:theme=\"@style/Theme.App.Fullscreen\" tools:ignore=\"LockedOrientationActivity\"> <intent-filter> <action android:name=\"android.intent.action.MAIN\" /> <category android:name=\"android.intent.category.LAUNCHER\" /> </intent-filter> </activity>\n"
                    + "\n\t\t<activity"
        )
        shouldWrite = true
    }

    if (shouldWrite) {
        manifestFile.writeText(textFromFile)
    }
}

fun loadEnv(target: LibraryBuildType, envFile: String) {
    val envProperties = loadProperties(envFile)!!
    envProperties.getProperty("ADMOB_APP_ID").let {
        target.resValue("string", "ADMOB_APP_ID", it)
        target.buildConfigField("String", "ADMOB_APP_ID", it)
    }
    envProperties.getProperty("BANNER_AD_UNIT_ID").let {
        target.resValue("string", "BANNER_AD_UNIT_ID", it)
        target.buildConfigField("String", "BANNER_AD_UNIT_ID", it)
    }
    envProperties.getProperty("INTERSTITIAL_AD_UNIT_ID").let {
        target.resValue("string", "INTERSTITIAL_AD_UNIT_ID", it)
        target.buildConfigField("String", "INTERSTITIAL_AD_UNIT_ID", it)
    }
    var billingType = "com.mmgsoft.modules.libs.helpers.BillingType.GOOGLE"
    var refundMoney = envProperties.getProperty("GOOGLE_REFUND_MONEY")
    if (gradle.startParameter.taskNames.find { it.toLowerCase().contains("amazon") } != null) {
        billingType = "com.mmgsoft.modules.libs.helpers.BillingType.AMAZON"
        refundMoney = envProperties.getProperty("AMAZON_REFUND_MONEY")
    }
    target.buildConfigField(
        "com.mmgsoft.modules.libs.helpers.BillingType",
        "BILLING_TYPE",
        billingType
    )
    target.buildConfigField("String[]", "REFUND_MONEY", refundMoney)
}

fun loadProperties(filename: String): Properties? {
    val file = file(filename)
    var properties: Properties? = null

    if (file.exists()) {
        file.inputStream().use { inputStream ->
            properties = Properties().also {
                it.load(inputStream)
            }
        }
    }

    return properties
}
