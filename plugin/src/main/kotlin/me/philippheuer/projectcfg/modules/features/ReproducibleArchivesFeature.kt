package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.AbstractArchiveTask

class ReproducibleArchivesFeature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return (ctx.project.isRootProjectWithoutSubprojectsOrSubproject()
                && (ctx.isProjectLanguage(ProjectLanguage.JAVA) || ctx.isProjectLanguage(ProjectLanguage.KOTLIN))
                && !ctx.project.pluginManager.hasPlugin("java-platform"))
    }

    override fun run() {
        enableReproducibleArchives(ctx.project)
    }

    companion object {
        fun enableReproducibleArchives(project: Project) {
            project.tasks.withType(AbstractArchiveTask::class.java).configureEach {
                it.isPreserveFileTimestamps = false
                it.isReproducibleFileOrder = true
            }
        }
    }
}
