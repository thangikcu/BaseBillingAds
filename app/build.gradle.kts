plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android-extensions")
//    id("maven-publish")
}

android {
/*
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }

        singleVariant("debug") {
            withSourcesJar()
        }
    }*/

    compileSdk = 32

    defaultConfig {
        applicationId = "com.mmgsoft.mmgbaseadsmodule"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation(project(mapOf("path" to ":BaseBillingAds")))

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.bumptech.glide:glide:4.13.2")
}


/*lateinit var sourcesArtifact: PublishArtifact

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets["main"].java.srcDirs)
    }
    artifacts {
        sourcesArtifact = archives(sourcesJar)
    }
}*/
/*
afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])

                artifact(sourcesArtifact)
//                artifact("$buildDir/outputs/aar/libs-release.aar")
                groupId = "com.github.thangikcu"
                artifactId = "BaseBillingAds"
                version = android.defaultConfig.versionName
            }

            register<MavenPublication>("debug") {
                from(components["debug"])

                artifact(sourcesArtifact)
//                artifact("$buildDir/outputs/aar/libs-debug.aar")
                groupId = "com.github.thangikcu"
                artifactId = "BaseBillingAds-debug"
                version = android.defaultConfig.versionName
            }
        }
    }
}*/
