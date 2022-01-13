package me.philippheuer.projectcfg.features

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.util.PluginHelper
import me.philippheuer.projectcfg.util.applyProject
import org.gradle.api.Project

class TestLoggingFeature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        // plugin
        project.applyProject("com.adarshr.test-logger")

        // configure
        project.extensions.configure(TestLoggerExtension::class.java) {
            if (PluginHelper.isCI() || PluginHelper.isIDEA()) {
                it.theme = ThemeType.PLAIN
            } else {
                it.theme = ThemeType.MOCHA_PARALLEL
            }
            it.slowThreshold = 1000
        }
    }
}