package me.philippheuer.projectcfg.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class JUnit5Feature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    private val junit5Version = "5.8.2"

    override fun check(): Boolean {
        return true
    }

    override fun run() {
        // junit5
        project.allprojects.forEach {
            it.dependencies.add("testImplementation", "org.junit.jupiter:junit-jupiter-api:$junit5Version")
            it.dependencies.add("testImplementation", "org.junit.jupiter:junit-jupiter-engine:$junit5Version")
            it.tasks.withType(Test::class.java).configureEach { test ->
                // use junit5
                test.useJUnitPlatform()

                // retest everything even if no changes have been made
                test.dependsOn("cleanTest")
            }
        }
    }
}