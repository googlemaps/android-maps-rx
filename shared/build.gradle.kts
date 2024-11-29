plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    lint {
        sarifOutput = file("$buildDir/reports/lint-results.sarif")
    }
    namespace = "com.google.maps.android.rx.shared"

    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xexplicit-api=strict"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")
}
