package me.philippheuer.projectcfg.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.applyProject
import org.gradle.api.Project

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
        project.applyProject("org.springframework.boot")

        project.run {
            tasks.getByName("jar").enabled = false // disable jar task, this would generate a plain jar

            // bom
            dependencies.enforcedPlatform("org.springframework.boot:spring-boot-dependencies:${DependencyVersion.springBootVersion}")

            // spring
            dependencies.add("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
            dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")

            // spring - log4j2
            configurations.getByName("implementation").exclude(mapOf("group" to "org.springframework.boot", "module" to "spring-boot-starter-logging"))
            dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-log4j2:${DependencyVersion.springBootVersion}")

            // lib - auto configure http/https proxy
            dependencies.add("implementation", "me.philippheuer.projectcfg.lib:springboot-proxy:${DependencyVersion.libVersion}")

            // metrics
            if (config.frameworkMetrics.get()) {
                dependencies.add("implementation", "io.micrometer:micrometer-core:1.8.1")
                dependencies.add("implementation", "io.micrometer:micrometer-registry-prometheus:1.8.1")

                // web project?
                if (DependencyUtils.hasDependency(project, listOf("implementation"), "org.springframework.boot:spring-boot-starter-web")) {
                    dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-actuator:${DependencyVersion.springBootVersion}")
                }
            }

            // native
            if (config.native.get()) {
                project.applyProject("org.springframework.experimental.aot")

                repositories.add(repositories.maven {
                    uri("https://repo.spring.io/libs-milestone/")
                })

                dependencies.add("implementation", "org.springframework.experimental:spring-native:0.11.1")
            }
        }
    }
}