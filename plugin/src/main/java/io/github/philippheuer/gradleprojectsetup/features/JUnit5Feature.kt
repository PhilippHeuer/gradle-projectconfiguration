package io.github.philippheuer.gradleprojectsetup.features

import io.github.philippheuer.gradleprojectsetup.domain.PluginModule
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class JUnit5Feature constructor(override var project: Project, override var config: io.github.philippheuer.gradleprojectsetup.ProjectSetupExtension) : PluginModule {
    private val junit5Version = "5.8.2"

    override fun check(): Boolean {
        return true
    }

    override fun run() {
        // junit5
        project.run {
            dependencies.add("testImplementation", "org.junit.jupiter:junit-jupiter-api:$junit5Version")
            dependencies.add("testImplementation", "org.junit.jupiter:junit-jupiter-engine:$junit5Version")
            tasks.withType(Test::class.java).configureEach {
                it.useJUnitPlatform()
                it.dependsOn("cleanTest")
            }
        }
    }
}