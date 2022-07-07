package me.philippheuer.projectcfg.util

import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.UnknownTaskException
import org.gradle.api.logging.LogLevel

fun Project.applyPlugin(pluginId: String) {
    PluginLogger.log(LogLevel.INFO, "applying plugin [$pluginId]")
    pluginManager.apply(pluginId)
}

/**
 * checks if the current project is the root project
 */
fun Project.isRootProject() : Boolean {
    return this.project == this.rootProject
}

/**
 * checks if the current project is the root project without subprojects OR a subproject
 */
fun Project.isRootProjectWithoutSubprojectsOrSubproject() : Boolean {
    return this.subprojects.size == 0 || (this.subprojects.size != 0 && this.project != this.rootProject)
}

fun Project.addDependency(configurationName: String, dependencyNotation: String) {
    PluginLogger.log(LogLevel.INFO, "applying dependency [$configurationName] $dependencyNotation")
    dependencies.add(configurationName, dependencyNotation)
}

fun Project.addDependency(dependencyNotation: String) {
    val configurationName = getPrimaryConfigurationName()
    PluginLogger.log(LogLevel.INFO, "applying dependency [$configurationName] $dependencyNotation")
    dependencies.add(configurationName, dependencyNotation)
}

fun Project.addPlatformDependency(dependencyNotation: String) {
    PluginLogger.log(LogLevel.INFO, "applying bom $dependencyNotation")
    dependencies.constraints.enforcedPlatform(dependencyNotation)
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

fun Project.hasTask(name: String): Boolean {
    return try {
        tasks.named(name)
        true
    } catch (e: UnknownTaskException) {
        false
    } catch (e: UnknownDomainObjectException) {
        false
    }
}

fun Project.getPrimaryConfigurationName(): String {
    if (pluginManager.hasPlugin("java")) {
        return "implementation"
    } else if (pluginManager.hasPlugin("java-library")) {
        return "api"
    }

    throw NotImplementedError("can't determinate default configurationName project. Didn't find either one of java or the java-library plugin.")
}