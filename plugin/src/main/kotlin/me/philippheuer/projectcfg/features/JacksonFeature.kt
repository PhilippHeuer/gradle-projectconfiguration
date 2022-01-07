package me.philippheuer.projectcfg.features

import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.DependencyVersion
import org.gradle.api.JavaVersion
import org.gradle.api.Project

class JacksonFeature constructor(override var project: Project, override var config: me.philippheuer.projectcfg.ProjectConfigurationExtension) : PluginModule {
    companion object {
        fun applyConstraint(project: Project) {
            project.dependencies.enforcedPlatform("com.fasterxml.jackson:jackson-bom:${DependencyVersion.jacksonVersion}")
        }
    }

    override fun init() {
        applyConstraint(project)
    }

    override fun check(): Boolean {
        return DependencyUtils.hasDependency(project, listOf("implementation", "api"), "com.fasterxml.jackson.core:jackson-databind")
    }

    override fun run() {
        if (config.language.get() == ProjectLanguage.KOTLIN) {
            project.dependencies.add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin:${DependencyVersion.jacksonVersion}")
        }

        if (config.javaVersion.get() >= JavaVersion.VERSION_11) {
            project.dependencies.add("implementation", "com.fasterxml.jackson.module:jackson-module-blackbird:${DependencyVersion.jacksonVersion}")
        }
    }
}