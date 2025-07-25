plugins {
    kotlin("jvm") version "2.2.0" apply false
    id("com.gradle.plugin-publish") version "1.3.1" apply false
}

allprojects {
    group = PluginCoordinates.GROUP
    version = project.property("artifact.version") as String

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
