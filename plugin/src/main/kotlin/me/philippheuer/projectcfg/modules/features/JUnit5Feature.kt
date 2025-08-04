package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.addDependency
import me.philippheuer.projectcfg.util.addPlatformDependency
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

/**
 * JUnit5 Task Configuration
 */
class JUnit5Feature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.isProjectSourceModule()
    }

    override fun run() {
        configureJunitDependencies(ctx.project, ctx.config)
        configureTestTask(ctx)
    }

    companion object {
        private fun configureJunitDependencies(project: Project, config: ProjectConfigurationExtension) {
            // junit
            project.addPlatformDependency("testImplementation", "org.junit:junit-bom:${DependencyVersion.junit5Version}")
            project.addDependency("testImplementation", "org.junit.jupiter:junit-jupiter")
            project.addDependency("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")

            // kotlin
            if (ProjectLanguage.KOTLIN.valueEquals(config.language.get())) {
                project.addDependency("testImplementation", "org.jetbrains.kotlin:kotlin-test:${DependencyVersion.kotlinVersion}")
            }
        }

        private fun configureTestTask(ctx: IProjectContext) {
            ctx.project.tasks.withType(Test::class.java).configureEach { test ->
                PluginLogger.setContext(ctx.project, "${JUnit5Feature::class.java}")

                // use junit5
                PluginLogger.log(LogLevel.DEBUG, "setting [test.useJUnitPlatform()]")
                test.useJUnitPlatform() { jp ->
                    jp.excludeTags("integration")
                }

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

                // full retest, even if no changes have been made
                test.dependsOn("cleanTest")
            }
        }
    }
}
