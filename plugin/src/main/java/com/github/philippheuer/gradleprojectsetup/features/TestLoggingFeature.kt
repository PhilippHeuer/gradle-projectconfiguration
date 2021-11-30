package com.github.philippheuer.gradleprojectsetup.features

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.github.philippheuer.gradleprojectsetup.ProjectSetupExtension
import com.github.philippheuer.gradleprojectsetup.domain.PluginModule
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ObjectConfigurationAction

class TestLoggingFeature constructor(override var project: Project, override var config: ProjectSetupExtension) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        project.run {
            apply { action: ObjectConfigurationAction ->
                log(LogLevel.INFO, "applying gradle plugin [com.adarshr.test-logger]")
                action.plugin("com.adarshr.test-logger")
            }

            extensions.run {
                configure(TestLoggerExtension::class.java) {
                    it.setTheme("mocha-parallel")
                    it.slowThreshold = 1000
                }
            }
        }
    }
}