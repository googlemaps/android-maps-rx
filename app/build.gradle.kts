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
