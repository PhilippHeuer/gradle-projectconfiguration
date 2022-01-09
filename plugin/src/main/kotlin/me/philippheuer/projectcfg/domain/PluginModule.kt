package me.philippheuer.projectcfg.domain

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import org.gradle.api.Project

interface PluginModule {
    var project: Project // reference to the current project context
    var config: ProjectConfigurationExtension // reference to the plugin configuration

    /**
     * default init code, but always run before afterEvaluate when config is not available yet
     */
    fun init() {

    }

    /**
     * checks if this module should be enabled
     * @return boolean
     */
    fun check(): Boolean {
        return false
    }

    /**
     * configures this module / feature, only call after check
     */
    fun run() {

    }
}