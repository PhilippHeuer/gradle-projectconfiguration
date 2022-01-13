package me.philippheuer.projectcfg.domain

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import org.gradle.api.Project

class ProjectContext(
    override val project: Project,
    override val config: ProjectConfigurationExtension,
) : IProjectContext {
}