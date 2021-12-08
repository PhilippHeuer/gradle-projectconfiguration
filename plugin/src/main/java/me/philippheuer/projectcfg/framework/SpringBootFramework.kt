package me.philippheuer.projectcfg.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ObjectConfigurationAction

class SpringBootFramework constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    private val springBootVersion = "2.6.1"

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

            // spring
            dependencies.add("implementation", "org.springframework.boot:spring-boot-starter:$springBootVersion")
            dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test:$springBootVersion")
        }
    }
}