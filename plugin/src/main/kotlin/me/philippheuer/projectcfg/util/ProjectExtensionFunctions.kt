package me.philippheuer.projectcfg.util

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

fun Project.applyProject(projectId: String) {
    PluginLogger.log(LogLevel.INFO, "applying plugin [$projectId]")
    pluginManager.apply(projectId)
}

/**
 * some plugins should only be applied to the root project, if there are no subprojects
 */
fun Project.isRootProjectWithoutSubprojectsOrSubproject() : Boolean {
    return this.subprojects.size == 0 || (this.subprojects.size != 0 && this.rootProject != this)
}

fun Project.addDependency(configurationName: String, dependencyNotation: String) {
    PluginLogger.log(LogLevel.INFO, "applying dependency [$configurationName] $dependencyNotation")
    dependencies.add(configurationName, dependencyNotation)
}

fun Project.addConstraint(dependencyNotation: String, version: String) {
    val configurationNames = mutableListOf<String>();

    if (pluginManager.hasPlugin("java")) {
        configurationNames.add("implementation");
    }
    if (pluginManager.hasPlugin("java-library")) {
        configurationNames.add("api");
    }

    configurationNames.forEach { configurationName ->
        dependencies.constraints.add(configurationName, dependencyNotation) { constraint -> constraint.version { v -> v.strictly(version) } }
    }
}