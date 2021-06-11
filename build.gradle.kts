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

import com.google.maps.android.rx.androidExtension
import com.google.maps.android.rx.artifactId

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")
    }
}

plugins {
    `maven-publish`
    signing
}

// Shared configs across subprojects
allprojects {
    group = "com.google.maps.android"
    version = "0.1.0"

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
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "signing")

    val sourcesJar = task<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        val libraryExtension = (project.androidExtension as com.android.build.gradle.LibraryExtension)
        from(libraryExtension.sourceSets["main"].java.srcDirs)
    }

    val dokkaHtml = tasks.named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaHtml")
    val dokkaJavadoc = tasks.named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaJavadoc")
    val javadocJar = task<Jar>("javadocJar") {
        dependsOn(dokkaHtml)
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(buildDir.resolve("dokka/javadoc"))
    }

    publishing {
        publications {
            create<MavenPublication>("aar") {
                groupId = project.group as String
                artifactId = project.artifactId
                version = project.version as String

                pom {
                    name.set(project.name)
                    description.set("RxJava bindings for the Maps SDK for Android")
                    url.set("https://github.com/googlemaps/android-maps-rx")

                    scm {
                        connection.set("scm:git@github.com:googlemaps/android-maps-rx.git")
                        developerConnection.set("scm:git@github.com:googlemaps/android-maps-rx.git")
                        url.set("https://github.com/googlemaps/android-maps-rx")
                    }

                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
                        }
                    }

                    organization {
                        name.set("Google Inc.")
                        url.set("https://developers.google.com/maps")
                    }

                    developers {
                        developer {
                            name.set("Google Inc.")
                        }
                    }
                }

                afterEvaluate {
                    artifact(buildDir.resolve("outputs/aar/${project.name}-release.aar"))

                    artifact(javadocJar)
                    artifact(sourcesJar)
                }
            }
        }

        repositories {
            maven {
                name = "mavencentral"
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = properties["sonatypeUsername"] as String?
                    password = properties["sonatypePassword"] as String
                }
            }
        }
    }

    // Signing
    signing {
        sign(publishing.publications.findByName("aar"))
    }
}