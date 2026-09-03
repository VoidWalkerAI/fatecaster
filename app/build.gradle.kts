plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val releaseStoreFile = System.getenv("FATECASTER_UPLOAD_STORE_FILE")
val releaseStorePassword = System.getenv("FATECASTER_UPLOAD_STORE_PASSWORD")
val releaseKeyAlias = System.getenv("FATECASTER_UPLOAD_KEY_ALIAS")
val releaseKeyPassword = System.getenv("FATECASTER_UPLOAD_KEY_PASSWORD")
val releaseSigningAvailable = listOf(
    releaseStoreFile,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword
).all { !it.isNullOrBlank() }

android {
    namespace = "ai.voidwalker.fatecaster"
    compileSdk = 36

    defaultConfig {
        applicationId = "ai.voidwalker.fatecaster"

        minSdk = 23
        targetSdk = 36

        versionCode = 2
        versionName = "0.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (releaseSigningAvailable) {
        signingConfigs {
            create("release") {
                storeFile = file(releaseStoreFile!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
                storeType = "JKS"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (releaseSigningAvailable) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(
            org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
        )
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    implementation("androidx.activity:activity-compose:1.13.0")

    val composeBom = platform("androidx.compose:compose-bom:2026.06.00")
    implementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    testImplementation("junit:junit:4.13.2")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
