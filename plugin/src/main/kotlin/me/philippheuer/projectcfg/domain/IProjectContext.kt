package me.philippheuer.projectcfg.domain

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import org.gradle.api.Project

interface IProjectContext {
    // reference to the current project context
    val project: Project

    // reference to the plugin configuration
    val config: ProjectConfigurationExtension
}