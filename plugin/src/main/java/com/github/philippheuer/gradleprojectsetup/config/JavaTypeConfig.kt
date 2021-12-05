package com.github.philippheuer.gradleprojectsetup.config

import org.gradle.api.JavaVersion
import org.gradle.api.provider.Property

interface JavaTypeConfig {

    // java version
    val javaVersion: Property<JavaVersion>

}