package com.github.philippheuer.gradleprojectsetup.framework

import com.github.philippheuer.gradleprojectsetup.ProjectSetupExtension
import com.github.philippheuer.gradleprojectsetup.domain.PluginModule
import com.github.philippheuer.gradleprojectsetup.domain.ProjectFramework
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ObjectConfigurationAction

class SpringBootFramework constructor(override var project: Project, override var config: ProjectSetupExtension) : PluginModule {
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
        }
    }
}