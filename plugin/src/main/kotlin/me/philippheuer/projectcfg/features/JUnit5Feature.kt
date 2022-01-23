package me.philippheuer.projectcfg.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.addDependency
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.testretry.TestRetryTaskExtension

/**
 * JUnit5 Task Configuration
 */
class JUnit5Feature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        configureJunitDependencies(ctx.project, ctx.config)
        configureTestTask(ctx.project)
    }

    companion object {
        private fun configureJunitDependencies(project: Project, config: ProjectConfigurationExtension) {
            project.addDependency("testImplementation", "org.junit.jupiter:junit-jupiter-api:${DependencyVersion.junit5Version}")
            project.addDependency("testImplementation", "org.junit.jupiter:junit-jupiter-params:${DependencyVersion.junit5Version}")
            project.addDependency("testRuntimeOnly", "org.junit.jupiter:junit-jupiter-engine:${DependencyVersion.junit5Version}")

            if (ProjectLanguage.KOTLIN.valueEquals(config.language.get())) {
                project.addDependency("testImplementation", "org.jetbrains.kotlin:kotlin-test:${DependencyVersion.kotlinVersion}")
            }
        }

        private fun configureTestTask(project: Project) {
            project.tasks.withType(Test::class.java).configureEach { test ->
                // use junit5
                PluginLogger.log(LogLevel.DEBUG, "setting [test.useJUnitPlatform()]")
                test.useJUnitPlatform()

                // test logging
                test.testLogging.showExceptions = true
                PluginLogger.log(LogLevel.DEBUG, "setting [test.testLogging.showExceptions] to [${test.testLogging.showExceptions}]")
                test.testLogging.showStandardStreams = true
                PluginLogger.log(LogLevel.DEBUG, "setting [test.testLogging.showStandardStreams] to [${test.testLogging.showStandardStreams}]")
                test.testLogging.exceptionFormat = TestExceptionFormat.FULL
                PluginLogger.log(LogLevel.DEBUG, "setting [test.testLogging.exceptionFormat] to [${test.testLogging.exceptionFormat.name}]")

                // don't require tests
                test.filter { filter ->
                    filter.isFailOnNoMatchingTests = false
                    PluginLogger.log(LogLevel.DEBUG, "setting [test.filter.isFailOnNoMatchingTests] to [${filter.isFailOnNoMatchingTests}]")
                }

                // retry for flaky tests
                test.extensions.getByType(TestRetryTaskExtension::class.java).maxRetries.set(3)
                test.extensions.getByType(TestRetryTaskExtension::class.java).maxFailures.set(20)
                test.extensions.getByType(TestRetryTaskExtension::class.java).failOnPassedAfterRetry.set(true)

                // full retest, even if no changes have been made
                test.dependsOn("cleanTest")
            }
        }
    }
}