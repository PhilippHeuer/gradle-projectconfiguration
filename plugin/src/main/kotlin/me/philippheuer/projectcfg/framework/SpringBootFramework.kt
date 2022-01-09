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
import me.philippheuer.projectcfg.util.addDepdenency
import me.philippheuer.projectcfg.util.applyProject
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import java.io.File

class SpringBootFramework constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    companion object {
        fun configureLibrary(project: Project, config: ProjectConfigurationExtension) {
            project.run {
                // bom
                dependencies.enforcedPlatform("org.springframework.boot:spring-boot-dependencies:${DependencyVersion.springBootVersion}")

                // spring
                addDepdenency("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
                addDepdenency("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")
            }
        }

        fun configureApplication(project: Project, config: ProjectConfigurationExtension) {
            project.applyProject("org.springframework.boot")

            project.run {
                tasks.getByName("jar").enabled = false // disable jar task, this would generate a plain jar

                // bom
                dependencies.enforcedPlatform("org.springframework.boot:spring-boot-dependencies:${DependencyVersion.springBootVersion}")

                // spring
                addDepdenency("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
                addDepdenency("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")

                // spring - log4j2
                configurations.getByName("implementation").exclude(mapOf("group" to "org.springframework.boot", "module" to "spring-boot-starter-logging"))
                addDepdenency("implementation", "org.springframework.boot:spring-boot-starter-log4j2:${DependencyVersion.springBootVersion}")

                // metrics
                if (config.frameworkMetrics.get()) {
                    addDepdenency("implementation", "io.micrometer:micrometer-core:1.8.1")
                    addDepdenency("implementation", "io.micrometer:micrometer-registry-prometheus:1.8.1")

                    // web project
                    if (DependencyUtils.hasDependency(project, listOf("implementation"), "org.springframework.boot:spring-boot-starter-web")) {
                        addDepdenency("implementation", "org.springframework.boot:spring-boot-starter-actuator:${DependencyVersion.springBootVersion}")
                    }
                }

                // native
                if (config.native.get()) {
                    project.applyProject("org.springframework.experimental.aot")

                    // repository and dependency
                    repositories.add(repositories.maven {
                        it.url = uri("https://repo.spring.io/release")
                    })
                    addDepdenency("implementation", "org.springframework.experimental:spring-native:${DependencyVersion.springNativeVersion}")

                    // task
                    tasks.withType(BootBuildImage::class.java).configureEach { image ->
                        image.builder = "paketobuildpacks/builder:tiny"
                        image.buildpacks = listOf("gcr.io/paketo-buildpacks/java-native-image:7.4.0")
                        image.environment = mapOf(
                            "BP_NATIVE_IMAGE" to "true"
                        )
                    }
                }
            }
        }

        fun configDefaults(project: Project, config: ProjectConfigurationExtension) {
            val properties = mutableMapOf(
                // logging
                "logging.level.root" to "INFO",
            )

            // db migrations
            if (config.frameworkDbMigrate.get()) {
                properties["spring.flyway.baselineOnMigrate"] = "true"
                properties["spring.flyway.baselineVersion"] = "0"
                properties["spring.flyway.locations"] = "classpath:db/migration"
            }

            // manage file
            PluginHelper.createOrUpdatePropertyFile("src/main/resources/application-default.properties", properties, managed = true)
        }
    }

    override fun check(): Boolean {
        return ProjectFramework.SPRINGBOOT.valueEquals(config.framework.get())
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