plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.gradle.plugin.publish) apply false
}

allprojects {
    group = PluginCoordinates.GROUP
    version = project.property("artifact.version") as String

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
