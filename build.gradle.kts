import com.google.maps.android.rx.artifactId

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
    }
}

allprojects {
    group = "com.google.maps.android"
    version = "0.1.0"

    repositories {
        google()
        jcenter()
    }
}

subprojects {
    if (project.artifactId() == null) return@subprojects

    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")

    // TODO apply shared publishing
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
