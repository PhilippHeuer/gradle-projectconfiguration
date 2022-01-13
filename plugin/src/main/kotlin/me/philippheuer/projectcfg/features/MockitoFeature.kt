package me.philippheuer.projectcfg.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.Project

class MockitoFeature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.project.isRootProjectWithoutSubprojectsOrSubproject()
    }

    override fun run() {
        applyDependencies(ctx.project, ctx.config)
    }

    companion object {
        fun applyDependencies(project: Project, config: ProjectConfigurationExtension) {
            project.dependencies.add("testImplementation", "org.mockito:mockito-core:${DependencyVersion.mockitoVersion}")
            project.dependencies.add("testImplementation", "org.mockito:mockito-inline:${DependencyVersion.mockitoVersion}")

            // kotlin
            if (ProjectLanguage.KOTLIN == config.language.get()) {
                project.dependencies.add("testImplementation", "org.mockito.kotlin:mockito-kotlin:${DependencyVersion.mockitoKotlinVersion}")
            }
        }
    }
}