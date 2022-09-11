package me.philippheuer.projectcfg.modules.framework

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.modules.framework.tasks.SpringConfigurationTask
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.addDependency
import me.philippheuer.projectcfg.util.addPlatformDependency
import me.philippheuer.projectcfg.util.applyPlugin
import org.gradle.api.Project

private const val CONFIG_TASK_NAME = "projectcfg-resources-springapp-properties"

class SpringBootFramework constructor(override var ctx: IProjectContext) : PluginModule {
    override fun init() {
        applyConstraint(ctx)
    }

    override fun check(): Boolean {
        return ctx.isProjectFramework(ProjectFramework.SPRINGBOOT)
    }

    override fun run() {
        if (ctx.isProjectType(ProjectType.LIBRARY)) {
            configureLibrary(ctx.project)
        } else if (ctx.isProjectType(ProjectType.APP)) {
            configureApplication(ctx)
            configDefaults(ctx)
        }
    }

    companion object {
        fun applyConstraint(ctx: IProjectContext) {
            // bom
            ctx.project.addPlatformDependency("org.springframework.boot:spring-boot-dependencies:${DependencyVersion.springBootVersion}")
        }

        fun configureLibrary(project: Project) {
            project.run {
                // spring
                addDependency("api", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
                addDependency("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")
            }
        }

        fun configureApplication(ctx: IProjectContext) {
            ctx.project.applyPlugin("org.springframework.boot")

            ctx.project.run {
                // spring
                addDependency("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
                addDependency("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")
                addDependency("developmentOnly", "org.springframework.boot:spring-boot-devtools:${DependencyVersion.springBootVersion}")

                // disable plain-jar task
                tasks.getByName("jar").enabled = false // disable jar task, this would generate a plain jar

                // spring - log4j2
                configurations.getByName("implementation").exclude(mapOf("group" to "org.springframework.boot", "module" to "spring-boot-starter-logging"))
                addDependency("implementation", "org.springframework.boot:spring-boot-starter-log4j2:${DependencyVersion.springBootVersion}")
                addDependency("implementation", "org.apache.logging.log4j:log4j:${DependencyVersion.log4j2Version}")
                addDependency("implementation", "com.lmax:disruptor:${DependencyVersion.disruptorVersion}")

                // metrics
                if (ctx.config.frameworkMetrics.get()) {
                    configureMetrics(ctx)
                }

                // native
                if (ctx.config.native.get()) {
                    configureNative(ctx)
                }
            }
        }

        fun configureMetrics(ctx: IProjectContext) {
            ctx.project.run {
                addDependency("implementation", "io.micrometer:micrometer-core:${DependencyVersion.micrometerVersion}")
                addDependency("implementation", "io.micrometer:micrometer-registry-prometheus:${DependencyVersion.micrometerVersion}")

                // web project
                if (DependencyUtils.hasDependency(project, listOf("implementation"), "org.springframework.boot:spring-boot-starter-web")) {
                    addDependency("implementation", "org.springframework.boot:spring-boot-starter-actuator:${DependencyVersion.springBootVersion}")
                } else if (DependencyUtils.hasDependency(project, listOf("implementation"), "org.springframework.boot:spring-boot-starter-webflux")) {
                    addDependency("implementation", "org.springframework.boot:spring-boot-starter-actuator:${DependencyVersion.springBootVersion}")
                }
            }
        }

        fun configureNative(ctx: IProjectContext) {
            /*
            ctx.project.run {
                applyPlugin("org.springframework.experimental.aot")

                // repository and dependency
                repositories.add(repositories.maven {
                    it.url = uri("https://repo.spring.io/release")
                })
                addDependency("implementation", "org.springframework.experimental:spring-native:${DependencyVersion.springNativeVersion}")

                // task
                tasks.withType(BootBuildImage::class.java).configureEach { image ->
                    image.builder = "paketobuildpacks/builder:tiny"
                    image.buildpacks = listOf("gcr.io/paketo-buildpacks/java-native-image:7.4.0")
                    image.environment = mapOf(
                        "BP_NATIVE_IMAGE" to "true",
                        "HTTP_PROXY" to "",
                        "HTTPS_PROXY" to ""
                    )
                }
            }
            */
        }

        fun configDefaults(ctx: IProjectContext) {
            // properties edit task
            val task = ctx.project.tasks.register(CONFIG_TASK_NAME, SpringConfigurationTask::class.java) {
                it.config = ctx.config
            }
            ctx.project.tasks.matching { it.name == "classes" }.configureEach {
                it.dependsOn(task)
                it.mustRunAfter("processResources")
            }
        }
    }
}
