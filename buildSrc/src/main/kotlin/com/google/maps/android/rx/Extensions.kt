package com.google.maps.android.rx

import org.gradle.api.Project

/**
 * Returns the artifactId of this project. Returns null if the project is not a publishable
 * project.
 */
fun Project.artifactId() : String? =
    if (name == "maps-rx") name else null
