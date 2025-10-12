plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.gradle.plugin.publish) apply false
    id("signing")
}

allprojects {
    apply(plugin = "signing")

    group = PluginCoordinates.GROUP
    version = project.property("artifact.version") as String

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    // signing
    extensions.configure(SigningExtension::class.java) {
        useGpgCmd()
        isRequired = false
    }

    // toggle signing based on startParameters
    tasks.withType(Sign::class.java).configureEach {
        if (gradle.startParameter.taskNames.any { it.contains("publishToMavenLocal") }) {
            enabled = false
        }
    }
}
