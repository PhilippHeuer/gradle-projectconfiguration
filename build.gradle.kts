plugins {
    kotlin("jvm") version "2.0.20" apply false
    id("com.gradle.plugin-publish") version "1.2.2" apply false
}

allprojects {
    group = PluginCoordinates.GROUP
    version = project.property("artifact.version") as String

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
