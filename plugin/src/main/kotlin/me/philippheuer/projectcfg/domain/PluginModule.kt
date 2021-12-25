package me.philippheuer.projectcfg.domain

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.slf4j.LoggerFactory

interface PluginModule {
    companion object {
        private val log = LoggerFactory.getLogger(PluginModule::class.java)
    }

    var project: Project // reference to the current project context
    var config: ProjectConfigurationExtension // reference to the plugin configuration

    /**
     * Checks if this module should be enabled
     * @return boolean
     */
    fun check(): Boolean

    /**
     * Configures this module / feature, only call after check
     */
    fun run()

    /**
     * logs a message
     *
     * loglevel set, print to console for easy diagnosis
     * loglevel not set, forward to slf4j
     */
    fun log(logLevel: LogLevel, message: String) {
        if (config.logLevel.isPresent) {
            if (config.logLevel.get() <= logLevel) {
                println("$logLevel: [${project.name}] ${javaClass.simpleName} -> $message")
            }
        } else {
            // delegate to slf4j
            if (logLevel == LogLevel.DEBUG) {
                log.debug(message)
            } else if (logLevel == LogLevel.INFO) {
                log.info(message)
            } else if (logLevel == LogLevel.WARN) {
                log.warn(message)
            } else if (logLevel == LogLevel.ERROR) {
                log.error(message)
            } else if (logLevel == LogLevel.LIFECYCLE) {
                log.info(message)
            }
        }
    }
}