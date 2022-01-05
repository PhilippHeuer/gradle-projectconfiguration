pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include(
    ":plugin",
)

// artifact id's
project(":plugin").name = "projectcfg-gradle-plugin"
