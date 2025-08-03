package me.philippheuer.projectcfg.util

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency

class DependencyUtils {

    companion object {
        /**
         * Checks if the project contains a specific dependency.
         *
         * @param project The Gradle project to inspect.
         * @param configurationNames List of configuration names to check, e.g. ["implementation", "api"].
         * @param dependencyNotation The dependency string to look for, either "group" or "group:name".
         * @param resolve If true, checks resolved dependencies (including transitives), otherwise checks declared dependencies only.
         * @return true if any dependency is found, false otherwise.
         */
        fun hasDependency(
            project: Project,
            configurationNames: List<String>,
            dependencyNotation: String,
            resolve: Boolean = false
        ): Boolean {
            return hasOneOfDependency(
                project,
                configurationNames,
                listOf(dependencyNotation),
                resolve
            )
        }

        /**
         * Checks if the project contains **any** of the specified dependencies.
         *
         * @param project The Gradle project to inspect.
         * @param configurationNames List of configuration names to check, e.g. ["implementation", "api"].
         * @param dependencyNotations List of dependency strings to look for, either "group" or "group:name".
         * @param resolve If true, checks resolved dependencies (including transitives), otherwise checks declared dependencies only.
         * @return true if any dependency is found, false otherwise.
         */
        fun hasOneOfDependency(
            project: Project,
            configurationNames: List<String>,
            dependencyNotations: List<String>,
            resolve: Boolean = false
        ): Boolean {
            val configurations = project.configurations.filter { configurationNames.contains(it.name) }

            return if (resolve) {
                configurations.any { config ->
                    config.resolvedConfiguration.resolvedArtifacts.any { artifact ->
                        val dep = artifact.moduleVersion.id
                        dependencyNotations.any { notation ->
                            notation == dep.group || notation == "${dep.group}:${dep.name}"
                        }
                    }
                }
            } else {
                configurations.any { config ->
                    config.allDependencies.any { dep ->
                        dependencyNotations.any { notation ->
                            notation == dep.group || notation == "${dep.group}:${dep.name}"
                        }
                    }
                }
            }
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