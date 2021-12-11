package me.philippheuer.projectcfg.features

import com.adarshr.gradle.testlogger.TestLoggerExtension
import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

class TestLoggingFeature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    private val isIDEA = System.getProperty("idea.fatal.error.notification") != null

    override fun check(): Boolean {
        // do not apply plugins when running in intellij
        if (isIDEA) {
            return false
        }

        return true
    }

    override fun run() {
        // plugin
        log(LogLevel.INFO, "applying plugin [com.adarshr.test-logger]")
        project.pluginManager.apply("com.adarshr.test-logger")

        // configure
        project.extensions.configure(TestLoggerExtension::class.java) {
            it.setTheme("mocha-parallel")
            it.slowThreshold = 1000
        }
    }
}