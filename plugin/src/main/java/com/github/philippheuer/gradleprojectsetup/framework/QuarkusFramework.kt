package com.github.philippheuer.gradleprojectsetup.framework

import com.github.philippheuer.gradleprojectsetup.ProjectSetupExtension
import com.github.philippheuer.gradleprojectsetup.domain.PluginModule
import com.github.philippheuer.gradleprojectsetup.domain.ProjectFramework
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ObjectConfigurationAction
import org.slf4j.LoggerFactory

class QuarkusFramework constructor(override var project: Project, override var config: ProjectSetupExtension) : PluginModule {
    override fun check(): Boolean {
        if (ProjectFramework.QUARKUS == config.framework.get()) {
            return true
        }

        return false
    }

    override fun run() {
        project.run {
            apply { action: ObjectConfigurationAction ->
                log(LogLevel.INFO, "applying gradle plugin [io.quarkus]")
                action.plugin("io.quarkus")
            }
        }
    }
}