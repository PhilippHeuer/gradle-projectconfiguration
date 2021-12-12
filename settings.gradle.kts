pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include(
    ":plugin",
    ":lib-springboot-proxy",
)

// artifact id's
project(":lib-springboot-proxy").name = "springboot-proxy"
