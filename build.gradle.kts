plugins {
    kotlin("jvm") version "1.9.22" apply false
    id("com.gradle.plugin-publish") version "0.21.0" apply false
}

allprojects {
    group = PluginCoordinates.GROUP
    version = project.property("artifact.version") as String

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
