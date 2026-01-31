package me.philippheuer.projectcfg.util

import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.UnknownTaskException
import org.gradle.api.logging.LogLevel

fun Project.applyPlugin(pluginId: String) {
    if (!pluginManager.hasPlugin(pluginId)) {
        PluginLogger.log(LogLevel.INFO, "applying plugin [$pluginId]")
        pluginManager.apply(pluginId)
    } else {
        PluginLogger.log(LogLevel.DEBUG, "plugin [$pluginId] already applied, skipping")
    }
}

fun Project.isRootProject() : Boolean {
    return this.project == this.rootProject
}

/**
 * checks if the current project is the root project without subprojects OR a subproject
 */
fun Project.isRootProjectWithoutSubprojectsOrSubproject() : Boolean {
    return this.subprojects.isEmpty() || (this.subprojects.isNotEmpty() && this.project != this.rootProject)
}

fun Project.isRootProjectWithSubprojects() : Boolean {
    return this.subprojects.isNotEmpty() && this.project == this.rootProject
}

fun Project.addDependency(configurationName: String, dependencyNotation: String) {
    PluginLogger.log(LogLevel.INFO, "applying dependency [$configurationName] $dependencyNotation")
    dependencies.add(configurationName, dependencyNotation)
}

fun Project.addDependency(dependencyNotation: String) {
    addDependency(getPrimaryConfigurationName(), dependencyNotation)
}

fun Project.addDependencyIfAbsent(configurationName: String, dependencyNotation: String) {
    if (!DependencyUtils.hasOneOfDependency(this, listOf(configurationName), listOf(dependencyNotation), resolve = false)) {
        addDependency(configurationName, dependencyNotation)
    } else {
        PluginLogger.log(LogLevel.DEBUG, "dependency [$configurationName] $dependencyNotation already present, skipping")
    }
}

fun Project.addPlatformDependency(configurationName: String, dependencyNotation: String) {
    PluginLogger.log(LogLevel.INFO, "applying platform dependency [$configurationName] $dependencyNotation")
    dependencies.add(configurationName, dependencies.enforcedPlatform(dependencyNotation))
}

fun Project.addPlatformDependencyIfAbsent(configurationName: String, dependencyNotation: String) {
    val configuration = configurations.getByName(configurationName)
    val (group, name) = dependencyNotation.substringBeforeLast(":").split(":", limit = 2)

    val alreadyPresent = configuration.dependencies.any { dep -> dep.group == group && dep.name == name }
    if (alreadyPresent) {
        PluginLogger.log(LogLevel.DEBUG, "platform dependency [$configurationName] $dependencyNotation already present, skipping")
        return
    }

    addPlatformDependency(configurationName, dependencyNotation)
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