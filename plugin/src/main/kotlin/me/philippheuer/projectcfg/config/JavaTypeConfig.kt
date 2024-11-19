package me.philippheuer.projectcfg.config

import org.gradle.api.JavaVersion
import org.gradle.api.provider.Property

interface JavaTypeConfig {

    // java version
    val javaVersion: Property<JavaVersion>

    // java toolchain version
    val javaToolchainVersion: Property<JavaVersion>
}
