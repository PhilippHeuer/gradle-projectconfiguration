package me.philippheuer.projectcfg.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyVersion
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class MockitoFeature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        project.dependencies.add("testImplementation", "org.mockito:mockito-core:${DependencyVersion.mockitoVersion}")
        project.dependencies.add("testImplementation", "org.mockito:mockito-inline:${DependencyVersion.mockitoVersion}")

        // kotlin
        if (ProjectLanguage.KOTLIN == config.language.get()) {
            project.dependencies.add("testImplementation", "org.mockito.kotlin:mockito-kotlin:${DependencyVersion.mockitoKotlinVersion}")
        }
    }
}