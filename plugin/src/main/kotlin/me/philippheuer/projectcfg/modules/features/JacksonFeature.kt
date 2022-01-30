package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyVersion
import org.gradle.api.JavaVersion
import org.gradle.api.Project

class JacksonFeature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun init() {
        applyConstraint(ctx.project)
    }

    override fun check(): Boolean {
        return ctx.hasProjectDependency("com.fasterxml.jackson.core:jackson-databind")
    }

    override fun run() {
        if (ctx.config.language.get() == ProjectLanguage.KOTLIN) {
            ctx.project.dependencies.add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin:${DependencyVersion.jacksonVersion}")
        }

        if (ctx.config.javaVersion.get() >= JavaVersion.VERSION_11) {
            ctx.project.dependencies.add("implementation", "com.fasterxml.jackson.module:jackson-module-blackbird:${DependencyVersion.jacksonVersion}")
        }
    }

    companion object {
        fun applyConstraint(project: Project) {
            project.dependencies.enforcedPlatform("com.fasterxml.jackson:jackson-bom:${DependencyVersion.jacksonVersion}")
        }
    }
}