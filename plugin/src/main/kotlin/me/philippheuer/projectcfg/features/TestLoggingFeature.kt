package me.philippheuer.projectcfg.features

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

class TestLoggingFeature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    private val isIDEA = System.getProperty("idea.fatal.error.notification") != null
    private val isCI = "true".equals(System.getenv("CI"), true)

    override fun check(): Boolean {
        return true
    }

    override fun run() {
        // plugin
        log(LogLevel.INFO, "applying plugin [com.adarshr.test-logger]")
        project.pluginManager.apply("com.adarshr.test-logger")

        // configure
        project.extensions.configure(TestLoggerExtension::class.java) {
            if (isCI || isIDEA) {
                it.theme = ThemeType.PLAIN
            } else {
                it.theme = ThemeType.MOCHA_PARALLEL
            }
            it.slowThreshold = 1000
        }
    }
}