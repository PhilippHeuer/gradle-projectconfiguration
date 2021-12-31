package me.philippheuer.projectcfg.util

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

fun Project.applyProject(projectId: String) {
    PluginLogger.log(LogLevel.INFO, "applying plugin [$projectId]")
    pluginManager.apply(projectId)
}
