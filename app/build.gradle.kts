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
    implementation("com.google.maps.android:maps-rx:1.0.0")

    // RxJava bindings for the Places SDK
    implementation("com.google.maps.android:places-rx:1.0.0")

    // Latest Maps SDK, Places SDK, and RxJava
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.libraries.places:places:4.1.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")

    // implementation project(":maps-rx")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.trello.rxlifecycle4:rxlifecycle-android-lifecycle-kotlin:4.0.2")
    implementation("com.google.maps.android:maps-ktx:5.1.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
}

secrets {
    defaultPropertiesFileName = "local.defaults.properties"
}
