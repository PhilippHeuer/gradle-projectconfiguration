package me.philippheuer.projectcfg.spring.springframework

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.addDependency
import me.philippheuer.projectcfg.util.addPlatformDependency
import me.philippheuer.projectcfg.util.applyPlugin
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.logging.LogLevel
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar

class SpringBootFramework(override var ctx: IProjectContext) : PluginModule {
    override fun init() {

    }

    override fun check(): Boolean {
        return ctx.isProjectFramework(ProjectFramework.SPRINGBOOT)
    }

    override fun run() {
        if (ctx.isProjectType(ProjectType.APP)) {
            PluginLogger.log(LogLevel.INFO, "applying spring boot application configuration")
            configureSpringApplication(ctx)
            postProcessApplicationProperties(ctx)
        } else if (ctx.isProjectType(ProjectType.LIBRARY)) {
            PluginLogger.log(LogLevel.INFO, "applying spring boot library configuration")
            configureSpringLibrary(ctx)
        }
    }

    companion object {
        fun configureSpringLibrary(ctx: IProjectContext) {
            ctx.project.run {
                // spring
                addPlatformDependency("implementation", SpringBootPlugin.BOM_COORDINATES)
                addDependency("implementation", "org.springframework.boot:spring-boot-starter")
                addDependency("testImplementation", "org.springframework.boot:spring-boot-starter-test")
            }
        }

        fun configureSpringApplication(ctx: IProjectContext) {
            ctx.project.run {
                applyPlugin("org.springframework.boot")

                // spring
                addPlatformDependency("implementation", SpringBootPlugin.BOM_COORDINATES)
                addDependency("implementation", "org.springframework.boot:spring-boot-starter")
                addDependency("testImplementation", "org.springframework.boot:spring-boot-starter-test")
                addDependency("runtimeOnly", "org.springframework.boot:spring-boot-devtools")

                // disable plain-jar task
                tasks.getByName("jar").enabled = false // disable jar task, this would generate a plain jar

                // exclude duplicate files instead of failing
                tasks.withType(BootJar::class.java).configureEach { bootJar ->
                    bootJar.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                }
            }
        }

        fun postProcessApplicationProperties(ctx: IProjectContext) {
            // properties edit task
            val task = ctx.project.tasks.register("postProcessSpringProperties", SpringApplicationProperties::class.java)
            ctx.project.tasks.matching { it.name == "classes" }.configureEach {
                it.dependsOn(task)
                it.mustRunAfter("processResources")
            }
        }
    }
}
