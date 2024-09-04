pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include(
    ":plugin",
    ":plugin-spring"
)

// artifact id's
project(":plugin").name = "projectcfg-gradle-plugin"
project(":plugin-spring").name = "projectcfg-spring-gradle-plugin"
