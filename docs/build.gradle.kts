plugins {
    kotlin("jvm") apply false
    id("org.jetbrains.dokka")
}

dependencies {
    dokka(project(":maps-rx"))
    dokka(project(":places-rx"))
}

dokka {
    moduleName.set("Maps Android Rx")
}