package me.philippheuer.projectcfg.domain

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.util.DependencyUtils
import org.gradle.api.Project

interface IProjectContext {
    // reference to the current project context
    val project: Project

    // reference to the plugin configuration
    val config: ProjectConfigurationExtension

    fun isRootProject(): Boolean {
        return project == project.rootProject
    }

    fun isProjectLanguage(language: IProjectLanguage): Boolean {
        return language.valueEquals(config.language.get())
    }

    fun isProjectType(type: IProjectType): Boolean {
        return type.valueEquals(config.type.get())
    }

    fun isProjectTypeIn(type: Iterable<IProjectType>): Boolean {
        return type.any { it.valueEquals(config.type.get()) }
    }

    fun isProjectFramework(framework: IProjectFramework): Boolean {
        return framework.valueEquals(config.framework.get())
    }

    fun hasProjectDependency(dependencyNotation: String): Boolean {
        return DependencyUtils.hasDependency(project, listOf("implementation", "api"), dependencyNotation)
    }

    /**
     * Checks if the project is a source module, as we don't want to apply certain features to platform modules or gradle version catalog modules
     */
    fun isProjectSourceModule(): Boolean {
        return !project.pluginManager.hasPlugin("java-platform") && !project.pluginManager.hasPlugin("version-catalog") && (project.pluginManager.hasPlugin("java") || project.pluginManager.hasPlugin("java-library"))
    }
}
