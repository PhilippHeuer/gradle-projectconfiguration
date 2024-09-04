package me.philippheuer.projectcfg.spring

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import org.gradle.api.Project
import javax.inject.Inject

open class SpringProjectConfigurationExtension @Inject constructor(project: Project) : ProjectConfigurationExtension(project) {

}
