package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.addDependency

class LoggingLibraryFeature(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.isProjectSourceModule()
    }

    override fun run() {
        var configurationName = "implementation"
        if (ctx.isProjectType(ProjectType.LIBRARY)) {
            configurationName = "api"
        }

        // java
        if (ProjectLanguage.JAVA.valueEquals(ctx.config.language.get())) {
            ctx.project.addDependency(configurationName, "org.slf4j:slf4j-api:${DependencyVersion.slf4jVersion}")
        }

        // kotlin
        if (ProjectLanguage.KOTLIN.valueEquals(ctx.config.language.get())) {
            ctx.project.addDependency(configurationName, "io.github.microutils:kotlin-logging:${DependencyVersion.kotlinLoggingVersion}")
        }
    }
}