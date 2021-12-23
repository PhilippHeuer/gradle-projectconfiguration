package me.philippheuer.projectcfg.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ObjectConfigurationAction

class QuarkusFramework constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectFramework.QUARKUS == config.framework.get()
    }

    override fun run() {
        log(LogLevel.INFO, "applying plugin [io.quarkus]")
        project.pluginManager.apply("io.quarkus")
    }
}