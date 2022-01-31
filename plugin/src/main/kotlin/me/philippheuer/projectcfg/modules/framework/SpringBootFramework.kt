package me.philippheuer.projectcfg.modules.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
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
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

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
            configureApplication(ctx.project, ctx.config)
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
                addDependency("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
                addDependency("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")
            }
        }

        fun configureApplication(project: Project, config: ProjectConfigurationExtension) {
            project.applyPlugin("org.springframework.boot")

            project.run {
                // spring
                addDependency("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
                addDependency("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")

                // disable plain-jar task
                tasks.getByName("jar").enabled = false // disable jar task, this would generate a plain jar

                // spring - log4j2
                configurations.getByName("implementation").exclude(mapOf("group" to "org.springframework.boot", "module" to "spring-boot-starter-logging"))
                addDependency("implementation", "org.springframework.boot:spring-boot-starter-log4j2:${DependencyVersion.springBootVersion}")
                addDependency("implementation", "com.lmax:disruptor:${DependencyVersion.disruptorVersion}")

                // metrics
                if (config.frameworkMetrics.get()) {
                    addDependency("implementation", "io.micrometer:micrometer-core:1.8.1")
                    addDependency("implementation", "io.micrometer:micrometer-registry-prometheus:1.8.1")

                    // web project
                    if (DependencyUtils.hasDependency(project, listOf("implementation"), "org.springframework.boot:spring-boot-starter-web")) {
                        addDependency("implementation", "org.springframework.boot:spring-boot-starter-actuator:${DependencyVersion.springBootVersion}")
                    }
                }

                // native
                if (config.native.get()) {
                    project.applyPlugin("org.springframework.experimental.aot")

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
                            "BP_NATIVE_IMAGE" to "true"
                        )
                    }
                }
            }
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