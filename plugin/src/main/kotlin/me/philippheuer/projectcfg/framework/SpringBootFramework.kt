package me.philippheuer.projectcfg.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.framework.SpringBootFramework.Companion.configDefaults
import me.philippheuer.projectcfg.framework.SpringBootFramework.Companion.configureApplication
import me.philippheuer.projectcfg.framework.SpringBootFramework.Companion.configureLibrary
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.PluginHelper
import me.philippheuer.projectcfg.util.PluginLogger.Companion.config
import me.philippheuer.projectcfg.util.PluginLogger.Companion.project
import me.philippheuer.projectcfg.util.applyProject
import org.gradle.api.Project
import java.io.File

class SpringBootFramework constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    companion object {
        fun configureLibrary(project: Project, config: ProjectConfigurationExtension) {
            project.run {
                // bom
                dependencies.enforcedPlatform("org.springframework.boot:spring-boot-dependencies:${DependencyVersion.springBootVersion}")

                // spring
                dependencies.add("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
                dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")
            }
        }

        fun configureApplication(project: Project, config: ProjectConfigurationExtension) {
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

                // metrics
                if (config.frameworkMetrics.get()) {
                    dependencies.add("implementation", "io.micrometer:micrometer-core:1.8.1")
                    dependencies.add("implementation", "io.micrometer:micrometer-registry-prometheus:1.8.1")

                    // web project
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

        fun configDefaults(project: Project, config: ProjectConfigurationExtension) {
            val properties = mutableMapOf<String, String>()

            // manage file
            PluginHelper.createOrUpdatePropertyFile("src/main/resources/application-default.properties", properties, managed = true)
        }
    }

    override fun check(): Boolean {
       return ProjectFramework.SPRINGBOOT == config.framework.get()
    }

    override fun run() {
        if (ProjectType.LIBRARY == config.type.get()) {
            configureLibrary(project, config)
        } else if (ProjectType.APP == config.type.get()) {
            configureApplication(project, config)
            configDefaults(project, config)
        }
    }
}