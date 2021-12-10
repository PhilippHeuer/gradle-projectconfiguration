package me.philippheuer.projectcfg.util

import org.gradle.api.Project

class DependencyUtils {

    companion object {
        /**
         * hasDependency can be used to check if a project contains a certain dependency
         */
        fun hasDependency(project: Project, configurationNames: List<String>, dependencyNotation: String): Boolean {
            project.configurations.filter { configurationNames.contains(it.name) }.forEach { configuration ->
                configuration.allDependencies.forEach { dep ->
                    if (dependencyNotation == dep.group || dependencyNotation == "${dep.group}:${dep.name}") {
                        return true
                    }
                }
            }

            return false
        }
    }

}