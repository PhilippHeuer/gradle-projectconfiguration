package me.philippheuer.projectcfg.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.DependencyVersion
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

class SpringBootFramework constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
       return ProjectFramework.SPRINGBOOT == config.framework.get()
    }

    override fun run() {
        if (ProjectType.LIBRARY == config.type.get()) {
            configureLibrary()
        } else if (ProjectType.APP == config.type.get()) {
            configureApplication()
        }
    }

    fun configureLibrary() {
        // bom
        project.dependencies.enforcedPlatform("org.springframework.boot:spring-boot-dependencies:${DependencyVersion.springBootVersion}")

        // spring
        project.dependencies.add("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
        project.dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")
    }

    fun configureApplication() {
        project.run {
            if (config.type.get() == ProjectType.APP) {
                log(LogLevel.INFO, "applying plugin [org.springframework.boot]")
                pluginManager.apply("org.springframework.boot")

                tasks.getByName("jar").enabled = false // disable jar task, this would generate a plain jar
            }

            // bom
            dependencies.enforcedPlatform("org.springframework.boot:spring-boot-dependencies:${DependencyVersion.springBootVersion}")

            // spring
            dependencies.add("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
            dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")

            // spring - log4j2
            configurations.getByName("implementation").exclude(mapOf("group" to "org.springframework.boot", "module" to "spring-boot-starter-logging"))
            dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-log4j2:${DependencyVersion.springBootVersion}")

            // metrics
            if (config.frameworkMetrics.get()) {
                dependencies.add("implementation", "io.micrometer:micrometer-core:1.8.1")

                // web project?
                if (DependencyUtils.hasDependency(project.rootProject, listOf("implementation"), "org.springframework.boot:spring-boot-starter-web")) {
                    dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-actuator:${DependencyVersion.springBootVersion}")
                    dependencies.add("implementation", "io.micrometer:micrometer-registry-prometheus:1.8.1")
                }
            }
        }
    }
}