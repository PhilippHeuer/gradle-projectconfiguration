plugins {
    kotlin("jvm") version "1.7.22" apply false
    id("com.gradle.plugin-publish") version "0.21.0" apply false
}

allprojects {
    group = PluginCoordinates.GROUP
    version = project.property("artifact.version") as String

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://repo.spring.io/libs-release/")
            content {
                includeGroup("org.springframework.experimental")
            }
        }
    }
}
