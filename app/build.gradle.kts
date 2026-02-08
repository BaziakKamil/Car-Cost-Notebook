import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
}

val versionMajor = 0
val versionMinor = 1
val versionPatch = 9

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
            manifestPlaceholders.put("crashlyticsCollectionEnabled", true)
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
        }

        getByName("debug") {
            manifestPlaceholders.put("crashlyticsCollectionEnabled", false)
            isDebuggable = true
        }

        create("qa") {
            manifestPlaceholders.put("crashlyticsCollectionEnabled", false)
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = true
        }

        create("staging") {
            manifestPlaceholders.put("crashlyticsCollectionEnabled", false)
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
        compose = true
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
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("androidx.activity:activity:1.10.1")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.core:core-splashscreen:1.0.1")

    //Compose
    implementation("androidx.compose.material:material:1.9.0")
    implementation("androidx.compose.compiler:compiler:1.5.15")
    implementation("androidx.compose.ui:ui-tooling-preview:1.9.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.9.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.9.0")

    // Material
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("com.google.android.material:material:1.13.0")

    // Architectural Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.3")

    // Room
    implementation("androidx.room:room-runtime:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")

    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.7.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Coroutine Lifecycle Scopes
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.3")

    // Navigation Components
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.3")
    implementation("androidx.navigation:navigation-compose:2.9.7")

    // Koin for Android
    implementation("io.insert-koin:koin-android:3.5.6")
    implementation("io.insert-koin:koin-androidx-compose:3.5.6")

    // Crashlytics
    implementation(platform("com.google.firebase:firebase-bom:34.2.0"))

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    testImplementation("io.insert-koin:koin-test:4.1.1")
    testImplementation("io.insert-koin:koin-test-junit4:4.1.1")
    testImplementation("io.mockk:mockk:1.14.5")
    androidTestImplementation("io.mockk:mockk-android:1.14.5")
    testImplementation("androidx.test:core:1.7.0")
    testImplementation("androidx.test:rules:1.7.0")
    testImplementation("androidx.test:runner:1.7.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.1.21")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
}