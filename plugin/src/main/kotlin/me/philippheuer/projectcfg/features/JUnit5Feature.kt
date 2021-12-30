package me.philippheuer.projectcfg.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyVersion
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

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

            // filter
            test.filter { filter ->
                filter.isFailOnNoMatchingTests = false
            }

            // test logging
            test.testLogging.showExceptions = true
            test.testLogging.showStandardStreams = true
            test.testLogging.events = setOf(
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
            )
            test.testLogging.exceptionFormat = TestExceptionFormat.FULL

            // retest everything even if no changes have been made
            test.dependsOn("cleanTest")
        }
    }
}
