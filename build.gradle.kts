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

import com.google.maps.android.rx.artifactId
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension


buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(libs.gradle)
        classpath(libs.secrets.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.dokka.gradle.plugin)
        classpath(libs.jacoco.android)
    }
}

plugins {
    id("org.jetbrains.dokka") version "2.0.0"
    id("com.vanniktech.maven.publish") version "0.34.0" apply false
}

// Shared configs
allprojects {
    group = "com.google.maps.android"
    version = "1.0.0"

    repositories {
        google()
        mavenCentral()
    }
}

// Publishing and signing info
subprojects {
    if (project.artifactId == null) return@subprojects

    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.vanniktech.maven.publish")
    apply(plugin = "com.mxalbert.gradle.jacoco-android")

    // Jacoco setup
    configure<JacocoPluginExtension> {
        toolVersion = "0.8.7"
    }

    tasks.withType<Test>().configureEach {
        extensions.configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }

    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral()
        signAllPublications()

        pom {
            name.set(project.name)
            description.set("RxJava bindings for the Maps SDK for Android")
            url.set("https://github.com/googlemaps/android-maps-rx")

            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }

            scm {
                connection.set("scm:git@github.com:googlemaps/android-maps-rx.git")
                developerConnection.set("scm:git@github.com:googlemaps/android-maps-rx.git")
                url.set("https://github.com/googlemaps/android-maps-rx")
            }

            organization {
                name.set("Google Inc.")
                url.set("https://developers.google.com/maps")
            }

            developers {
                developer {
                    id.set("google")
                    name.set("Google Inc.")
                }
            }
        }
    }
}
