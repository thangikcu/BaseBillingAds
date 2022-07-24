import com.android.build.api.dsl.LibraryBuildType
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("maven-publish")
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.thangikcu"
                artifactId = "BaseBillingAds"
                version = android.defaultConfig.versionName
            }

            register<MavenPublication>("debug") {
                from(components["debug"])

                groupId = "com.github.thangikcu"
                artifactId = "BaseBillingAds-debug"
                version = android.defaultConfig.versionName
            }
        }
    }
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")

    implementation("com.google.android.gms:play-services-ads:21.1.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    implementation("com.intuit.sdp:sdp-android:1.0.6")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.android.billingclient:billing-ktx:5.0.0")
    implementation("com.android.billingclient:billing:5.0.0")
    implementation("androidx.core:core-ktx:1.8.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.13.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")
    implementation("androidx.annotation:annotation:1.4.0")
//    implementation ("com.amazon.device:amazon-appstore-sdk:3.0.2")


    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(files("./libs/amazon-appstore-sdk-3.0.3.jar"))
    // UPDATE BUILD GRADLE
    // parser
    // database
    implementation("androidx.room:room-rxjava2:2.4.2")
    annotationProcessor("androidx.room:room-compiler:2.4.2")
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
}

fun loadProperties(filename: String): Properties? {
    val file = project.rootProject.file(filename)
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


tasks.register("generateKeystore") {

    val APPLICATION_ID = "com.android.pro.scanner"

    val keyAlias = "als_${APPLICATION_ID}_keystore_release_product"
    val keyPassword = "${APPLICATION_ID}_keystore_release_product"
    val storeFile = "keystore/${APPLICATION_ID}_keystore_release_product.jks"
    val storePassword = "${APPLICATION_ID}_keystore_release_product"

    if (!file(storeFile).exists()) {
        file("keystore").mkdir()

        project.exec {
            isIgnoreExitValue = true
            workingDir(projectDir)
            executable = "keytool"
            val args = listOf(
                "-genkey",
                "-v",
                "-keystore", storeFile,
                "-alias", keyAlias,
                "-storepass", keyPassword,
                "-keypass", storePassword,
                "-dname", "CN=Android Debug",
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-validity", "10000"
            )
            setArgs(args)
        }
    }
}