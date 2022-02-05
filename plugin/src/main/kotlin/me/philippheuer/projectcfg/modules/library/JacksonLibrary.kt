package me.philippheuer.projectcfg.modules.library

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectLibraries
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.addDependency
import me.philippheuer.projectcfg.util.addPlatformDependency
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject

/**
 * Jackson Library
 */
class JacksonLibrary constructor(override var ctx: IProjectContext) : PluginModule {
    override fun init() {
        ctx.project.addPlatformDependency("com.fasterxml.jackson:jackson-bom:${DependencyVersion.jacksonVersion}")
    }

    override fun check(): Boolean {
        return ctx.project.isRootProjectWithoutSubprojectsOrSubproject() && ctx.isProjectLibrary(ProjectLibraries.TEST_MOCKITO)
    }

    override fun run() {
        applyDependencies(ctx)
    }

    companion object {
        fun applyDependencies(ctx: IProjectContext) {
            ctx.project.addDependency("com.fasterxml.jackson.core:jackson-databind:${DependencyVersion.jacksonVersion}")

            // link https://github.com/FasterXML/jackson-modules-base/blob/2.13/blackbird/README.md
            if (ctx.config.javaVersion.get().isJava11Compatible) {
                ctx.project.addDependency("com.fasterxml.jackson.module:jackson-module-blackbird:${DependencyVersion.jacksonVersion}")
            }

            // kotlin
            if (ctx.isProjectLanguage(ProjectLanguage.KOTLIN)) {
                ctx.project.addDependency("com.fasterxml.jackson.module:jackson-module-kotlin:${DependencyVersion.jacksonVersion}")
            }
        }
    }
}