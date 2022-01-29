package me.philippheuer.projectcfg.library

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLibraries
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.addConstraint
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject

class Events4JLibrary constructor(override var ctx: IProjectContext) : PluginModule {
    override fun init() {
        ctx.project.addConstraint("com.github.philippheuer.events4j:events4j-api", DependencyVersion.events4jVersion)
        ctx.project.addConstraint("com.github.philippheuer.events4j:events4j-core", DependencyVersion.events4jVersion)
        ctx.project.addConstraint("com.github.philippheuer.events4j:events4j-handler-simple", DependencyVersion.events4jVersion)
        ctx.project.addConstraint("com.github.philippheuer.events4j:events4j-handler-reactor", DependencyVersion.events4jVersion)
        ctx.project.addConstraint("com.github.philippheuer.events4j:events4j-handler-spring", DependencyVersion.events4jVersion)
    }

    override fun check(): Boolean {
        return ctx.project.isRootProjectWithoutSubprojectsOrSubproject() && ctx.isProjectLibrary(ProjectLibraries.EVENTS4J)
    }

    override fun run() {
        if (ctx.isProjectType(ProjectType.LIBRARY)) {
            applyLibraryDependencies(ctx)
        } else {
            applyAppDependencies(ctx)
        }
    }

    companion object {
        fun applyLibraryDependencies(ctx: IProjectContext) {
            ctx.project.dependencies.add("api", "com.github.philippheuer.events4j:events4j-api:${DependencyVersion.events4jVersion}")
            ctx.project.dependencies.add("api", "com.github.philippheuer.events4j:events4j-core:${DependencyVersion.events4jVersion}")
        }

        fun applyAppDependencies(ctx: IProjectContext) {
            ctx.project.dependencies.add("implementation", "com.github.philippheuer.events4j:events4j-api:${DependencyVersion.events4jVersion}")
            ctx.project.dependencies.add("implementation", "com.github.philippheuer.events4j:events4j-core:${DependencyVersion.events4jVersion}")
        }
    }
}