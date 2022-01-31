package me.philippheuer.projectcfg.util

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency

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

        /**
         * hasDependency can be used to check if a project contains a certain dependency
         */
        fun hasOneOfDependency(project: Project, configurationNames: List<String>, dependencyNotation: List<String>): Boolean {
            project.configurations.filter { configurationNames.contains(it.name) }.forEach { configuration ->
                configuration.allDependencies.forEach { dep ->
                    dependencyNotation.forEach { dn ->
                        if (dn == dep.group || dn == "${dep.group}:${dep.name}") {
                            return true
                        }
                    }
                }
            }

            return false
        }

        /**
         * getDependencies collects all uzsed dependencies used in the project
         */
        fun getDependencies(project: Project, configurationNames: List<String>): List<Dependency> {
            val deps = mutableListOf<Dependency>()
            project.configurations.filter { configurationNames.contains(it.name) }.forEach { configuration ->
                configuration.dependencies.forEach {
                    deps.add(it)
                }
            }
            return deps
        }

        /**
         * is the dependency a module of the current project?
         */
        fun isProjectModule(project: Project, dep: Dependency): Boolean {
            project.allprojects.forEach { p ->
                if (p.group == dep.group && p.name == dep.name) {
                    return true
                }
            }

            return false
        }

        /**
         * resolves a configuration and returns all artifact versions
         *
         * use with caution: dependencies can not be modified after this!
         */
        fun getResolvedDependencies(project: Project, configurationNames: List<String>): Set<String> {
            val deps = mutableSetOf<String>()

            project.configurations.filter { configurationNames.contains(it.name) }.forEach { configuration ->
                configuration.resolvedConfiguration.resolvedArtifacts.filter { !it.id.componentIdentifier.displayName.startsWith("project :") }.forEach { dep ->
                    deps.add(dep.id.componentIdentifier.displayName)
                }
            }

            return deps
        }
    }

}