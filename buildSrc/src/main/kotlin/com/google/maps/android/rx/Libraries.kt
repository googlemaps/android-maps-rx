package com.google.maps.android.rx

object Libraries {
    const val androidGradlePlugin = "com.android.tools.build:gradle:4.1.2"

    object Android {
        const val material = "com.google.android.material:material:1.3.0"

        object AndroidX {
            private const val version = "1.2.0"
            const val appCompat = "androidx.appcompat:appcompat:$version"
            const val lifecycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0"
        }
    }

    object Gmp {
        const val maps = "com.google.android.gms:play-services-maps:17.0.1"
        const val mapsKtx = "com.google.maps.android:maps-ktx:2.3.0"
    }

    object Kotlin {
        private const val version = "1.4.21"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

    object Rx {
        const val rxAndroid = "io.reactivex.rxjava3:rxandroid:3.0.0"
        const val rxLifecycle = "com.trello.rxlifecycle4:rxlifecycle-android-lifecycle-kotlin:4.0.2"
        const val rxJava = "io.reactivex.rxjava3:rxjava:3.0.10"
    }
}