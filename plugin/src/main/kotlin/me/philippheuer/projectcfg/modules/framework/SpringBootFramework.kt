package me.philippheuer.projectcfg.modules.framework

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.modules.framework.tasks.SpringConfigurationTask
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.addDependency
import me.philippheuer.projectcfg.util.applyPlugin
import org.gradle.api.Project

private const val CONFIG_TASK_NAME = "projectcfg-resources-springapp-properties"

class SpringBootFramework constructor(override var ctx: IProjectContext) : PluginModule {
    override fun init() {

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
        fun configureLibrary(project: Project) {
            project.run {
                // spring
                addDependency("api", "org.springframework.boot:spring-boot-starter")
                addDependency("testImplementation", "org.springframework.boot:spring-boot-starter-test")
            }
        }

        fun configureApplication(ctx: IProjectContext) {
            ctx.project.applyPlugin("org.springframework.boot")

            ctx.project.run {
                // spring
                addDependency("implementation", "org.springframework.boot:spring-boot-starter")
                addDependency("testImplementation", "org.springframework.boot:spring-boot-starter-test:")
                addDependency("developmentOnly", "org.springframework.boot:spring-boot-devtools")

                // disable plain-jar task
                tasks.getByName("jar").enabled = false // disable jar task, this would generate a plain jar

                // spring - log4j2
                configurations.getByName("implementation").exclude(mapOf("group" to "org.springframework.boot", "module" to "spring-boot-starter-logging"))
                addDependency("implementation", "org.springframework.boot:spring-boot-starter-log4j2")
                addDependency("implementation", "org.apache.logging.log4j:log4j:${DependencyVersion.log4j2Version}")
                addDependency("implementation", "com.lmax:disruptor:${DependencyVersion.disruptorVersion}")
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
