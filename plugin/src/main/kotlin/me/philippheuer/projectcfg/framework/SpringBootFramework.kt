package me.philippheuer.projectcfg.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
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
        project.run {
            // bom
            dependencies.enforcedPlatform("org.springframework.boot:spring-boot-dependencies:${DependencyVersion.springBootVersion}")

            // spring
            dependencies.add("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
            dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")
        }
    }

    fun configureApplication() {
        project.run {
            log(LogLevel.INFO, "applying plugin [org.springframework.boot]")
            pluginManager.apply("org.springframework.boot")

            tasks.getByName("jar").enabled = false // disable jar task, this would generate a plain jar

            // bom
            dependencies.enforcedPlatform("org.springframework.boot:spring-boot-dependencies:${DependencyVersion.springBootVersion}")

            // spring
            dependencies.add("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
            dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")

            // spring - log4j2
            configurations.getByName("implementation").exclude(mapOf("group" to "org.springframework.boot", "module" to "spring-boot-starter-logging"))
            dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-log4j2:${DependencyVersion.springBootVersion}")
            dependencies.constraints.add("implementation", "org.apache.logging.log4j:log4j-core:2.17.0") { constraint ->
                constraint.version { v ->
                    v.strictly("[2.17,3[")
                    v.prefer("2.17.0")
                }
                constraint.because("Log4J is vulnerable to remote code execution (CVE-2021-44228 - Log4Shell) and denial of service (CVE-2021-45046)")
            }

            // lib - auto configure http/https proxy
            dependencies.add("implementation", "me.philippheuer.projectcfg.lib:springboot-proxy:${DependencyVersion.libVersion}")

            // metrics
            if (config.frameworkMetrics.get()) {
                dependencies.add("implementation", "io.micrometer:micrometer-core:1.8.1")

                // web project?
                if (DependencyUtils.hasDependency(project, listOf("implementation"), "org.springframework.boot:spring-boot-starter-web")) {
                    dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-actuator:${DependencyVersion.springBootVersion}")
                    dependencies.add("implementation", "io.micrometer:micrometer-registry-prometheus:1.8.1")
                }
            }
        }
    }
}