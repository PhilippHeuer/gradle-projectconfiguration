package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.addDependencyIfAbsent
import me.philippheuer.projectcfg.util.addPlatformDependencyIfAbsent
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * JUnit Task Configuration
 */
class JUnitFeature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.project.isRootProjectWithoutSubprojectsOrSubproject() && ctx.isProjectSourceModule()
    }

    override fun run() {
        configureJunitDependencies(ctx.project, ctx.config)
        configureTestTask(ctx)
    }

    companion object {
        private fun configureJunitDependencies(project: Project, config: ProjectConfigurationExtension) {
            // junit
            project.addPlatformDependencyIfAbsent("testImplementation", "org.junit:junit-bom:${DependencyVersion.junitVersion}")
            project.addDependencyIfAbsent("testImplementation", "org.junit.jupiter:junit-jupiter")
            project.addDependencyIfAbsent("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")

            // kotlin
            if (ProjectLanguage.KOTLIN.valueEquals(config.language.get())) {
                project.addDependencyIfAbsent("testImplementation", "org.jetbrains.kotlin:kotlin-test:${DependencyVersion.kotlinVersion}")
            }

            // junit 6 requires jdk 17 to run tests
            if (config.javaVersion.get() < JavaVersion.VERSION_17) {
                PluginLogger.log(LogLevel.INFO, "override jvm target for test-suite to [${config.javaVersion.get()}->17]")

                project.tasks.withType(JavaCompile::class.java).configureEach { task ->
                    if (task.name == "compileTestJava" || task.name == "compileJmhJava") {
                        task.options.release.set(17)
                    }
                }

                if (config.language.get() == ProjectLanguage.KOTLIN) {
                    project.tasks.withType(KotlinCompile::class.java).configureEach { task ->
                        if (task.name == "compileTestKotlin" || task.name == "compileJmhKotlin") {
                            task.compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
                        }
                    }
                }
            }
        }

        private fun configureTestTask(ctx: IProjectContext) {
            ctx.project.tasks.withType(Test::class.java).configureEach { test ->
                PluginLogger.setContext(ctx.project, "${JUnitFeature::class.java}")

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
