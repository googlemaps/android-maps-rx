android {
    lint {
        sarifOutput = file("$buildDir/reports/lint-results.sarif")
    }

    namespace = "com.google.maps.android.rx.places"

    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xexplicit-api=strict"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.libraries.places:places:4.1.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
}
