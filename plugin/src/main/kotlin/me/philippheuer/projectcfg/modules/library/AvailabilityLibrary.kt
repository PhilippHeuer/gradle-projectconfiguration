package me.philippheuer.projectcfg.modules.library

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLibraries
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.Project

class AvailabilityLibrary constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.project.isRootProjectWithoutSubprojectsOrSubproject() && ctx.isProjectLibrary(ProjectLibraries.TEST_AWAITABILITY)
    }

    override fun run() {
        applyDependencies(ctx.project)
    }

    companion object {
        fun applyDependencies(project: Project) {
            project.dependencies.add("testImplementation", "org.awaitility:awaitility:${DependencyVersion.availabilityVersion}")
        }
    }
}