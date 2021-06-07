// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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