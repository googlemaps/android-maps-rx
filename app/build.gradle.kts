/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("kotlin-android")
}

android {
    lint {
        sarifOutput = file("$buildDir/reports/lint-results.sarif")
    }

    buildFeatures {
        buildConfig = true
    }

    compileSdk = 35
    namespace = "com.google.maps.android.rx.demo"

    defaultConfig {
        applicationId = "com.google.maps.android.rx.demo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    // RxJava bindings for the Maps SDK
    implementation(libs.mapsRx)

    // RxJava bindings for the Places SDK
    implementation(libs.placesRx)

    // It is recommended to also include the latest Maps SDK, Places SDK and RxJava so you
    // have the latest features and bug fixes.
    implementation(libs.playServicesMaps)
    implementation(libs.places)
    implementation(libs.rxJava)
    implementation(libs.appCompat)
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.material)
    implementation(libs.rxLifecycle)
    implementation(libs.mapsKtx)
    implementation(libs.kotlinStdlib)

}

secrets {
    defaultPropertiesFileName = "local.defaults.properties"
}
