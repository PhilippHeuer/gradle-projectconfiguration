package me.philippheuer.projectcfg.domain

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import org.gradle.api.Project

interface PluginModule {
    var project: Project // reference to the current project context
    var config: ProjectConfigurationExtension // reference to the plugin configuration

    /**
     * checks if this module should be enabled
     * @return boolean
     */
    fun check(): Boolean

    /**
     * default init code, runs before afterEvaluate so config is not available yet
     */
    fun init() {

    }

    /**
     * configures this module / feature, only call after check
     */
    fun run()
}