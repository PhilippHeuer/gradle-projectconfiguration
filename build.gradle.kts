plugins {
    kotlin("jvm") version BuildPluginsVersion.KOTLIN apply false
    id("com.gradle.plugin-publish") version BuildPluginsVersion.PLUGIN_PUBLISH apply false
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
