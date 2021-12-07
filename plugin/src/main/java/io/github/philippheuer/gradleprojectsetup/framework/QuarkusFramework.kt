package io.github.philippheuer.gradleprojectsetup.framework

import io.github.philippheuer.gradleprojectsetup.ProjectSetupExtension
import io.github.philippheuer.gradleprojectsetup.domain.PluginModule
import io.github.philippheuer.gradleprojectsetup.domain.ProjectFramework
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ObjectConfigurationAction

class QuarkusFramework constructor(override var project: Project, override var config: ProjectSetupExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectFramework.QUARKUS == config.framework.get()
    }

    override fun run() {
        project.run {
            apply { action: ObjectConfigurationAction ->
                log(LogLevel.INFO, "applying plugin [io.quarkus]")
                action.plugin("io.quarkus")
            }
        }
    }
}