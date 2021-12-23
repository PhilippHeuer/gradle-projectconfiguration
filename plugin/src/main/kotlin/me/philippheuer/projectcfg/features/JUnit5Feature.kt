package me.philippheuer.projectcfg.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyVersion
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class JUnit5Feature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        configureJunitDependencies(project, config)
        configureTestTask(project)
    }

    fun configureJunitDependencies(project: Project, config: ProjectConfigurationExtension) {
        project.dependencies.add("testImplementation", "org.junit.jupiter:junit-jupiter-api:${DependencyVersion.junit5Version}")
        project.dependencies.add("testImplementation", "org.junit.jupiter:junit-jupiter-engine:${DependencyVersion.junit5Version}")

        if (config.language.get() == ProjectLanguage.KOTLIN) {
            project.dependencies.add("testImplementation", "org.jetbrains.kotlin:kotlin-test:${DependencyVersion.kotlinVersion}")
        }
    }

    fun configureTestTask(project: Project) {
        project.tasks.withType(Test::class.java).configureEach { test ->
            // use junit5
            test.useJUnitPlatform()

            // retest everything even if no changes have been made
            test.dependsOn("cleanTest")
        }
    }
}