package me.philippheuer.projectcfg.util

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.slf4j.LoggerFactory

class PluginLogger {
    companion object {
        private val log = LoggerFactory.getLogger(PluginModule::class.java)

        var project: Project? = null
        var config: ProjectConfigurationExtension? = null
        var module: PluginModule? = null

        /**
         * logs a message
         *
         * loglevel set, print to console for easy diagnosis
         * loglevel not set, forward to slf4j
         */
        fun log(logLevel: LogLevel, message: String) {
            if (config!!.logLevel.isPresent) {
                if (config!!.logLevel.get() <= logLevel) {
                    println("$logLevel: [${project!!.name}] ${module!!::class.java} -> $message")
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
}