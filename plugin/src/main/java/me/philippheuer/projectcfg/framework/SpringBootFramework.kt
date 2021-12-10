package me.philippheuer.projectcfg.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyUtils
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ObjectConfigurationAction

class SpringBootFramework constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    private val springBootVersion = "2.6.1"
    private val jacksonVersion = "2.13.0"

    override fun check(): Boolean {
       return ProjectFramework.SPRINGBOOT == config.framework.get()
    }

    override fun run() {
        project.run {
            apply { action: ObjectConfigurationAction ->
                log(LogLevel.INFO, "applying plugin [org.springframework.boot]")
                action.plugin("org.springframework.boot")
            }

            tasks.getByName("jar").enabled = false // disable jar task, this would generate a plain jar

            // bom
            dependencies.enforcedPlatform("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
            dependencies.enforcedPlatform("com.fasterxml.jackson:jackson-bom:$jacksonVersion")

            // spring
            dependencies.add("implementation", "org.springframework.boot:spring-boot-starter:$springBootVersion")
            dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test:$springBootVersion")

            // spring - log4j2
            configurations.getByName("implementation").exclude(mapOf("group" to "org.springframework.boot", "module" to "spring-boot-starter-logging"))
            dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-log4j2:$springBootVersion")

            // commons
            if (config.language.get() == ProjectLanguage.JAVA) {
                dependencies.add("implementation", "org.apache.commons:commons-lang3:3.12.0")
            }

            // jackson
            dependencies.add("implementation", "com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
            if (config.javaVersion.get() >= JavaVersion.VERSION_11) {
                dependencies.add("implementation", "com.fasterxml.jackson.module:jackson-module-blackbird:$jacksonVersion")
            }

            // metrics
            if (config.frameworkMetrics.get()) {
                dependencies.add("implementation", "io.micrometer:micrometer-core:1.8.1")

                // web project?
                if (DependencyUtils.hasDependency(project.rootProject, listOf("implementation"), "org.springframework.boot:spring-boot-starter-web")) {
                    dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-actuator:$springBootVersion")
                    dependencies.add("implementation", "io.micrometer:micrometer-registry-prometheus:1.8.1")
                }
            }
        }
    }
}