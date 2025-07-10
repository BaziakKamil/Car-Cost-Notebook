import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
}

val versionMajor = 0
val versionMinor = 1
val versionPatch = 8

android {

    compileSdk = 36
    defaultConfig {
        applicationId = "pl.kamilbaziak.carcostnotebook"
        minSdk = 33
        targetSdk = 36
        versionCode = versionMajor * 100 + versionMinor * 10 + versionPatch
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"
        manifestPlaceholders["hostName"] = "www.kamilbaziak.pl"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
            isDebuggable = false
        }

        getByName("debug") {
            isDebuggable = true
        }

        create("qa") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = true
        }

        create("staging") {
            initWith(getByName("debug"))
            manifestPlaceholders["hostName"] = "www.kamilbaziak.pl"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }

    namespace = "pl.kamilbaziak.carcostnotebook"
    packagingOptions {
        resources {
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Material
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("com.google.android.material:material:1.12.0")

    // Architectural Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    // Coroutine Lifecycle Scopes
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Navigation Components
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.6")

    // Koin for Android
    implementation("io.insert-koin:koin-android:4.0.2")

    // Crashlytics
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    testImplementation("io.insert-koin:koin-test:4.0.2")
    testImplementation("io.insert-koin:koin-test-junit4:4.0.2")
    testImplementation("io.mockk:mockk:1.13.16")
    androidTestImplementation("io.mockk:mockk-android:1.13.16")
    testImplementation("androidx.test:core:1.6.1")
    testImplementation("androidx.test:rules:1.6.1")
    testImplementation("androidx.test:runner:1.6.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.1.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
}