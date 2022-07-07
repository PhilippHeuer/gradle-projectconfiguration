package me.philippheuer.projectcfg.modules.report

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.modules.report.tasks.DependenciesReportResourcesTask

private const val DEPENDENCY_REPORT_TASK_NAME = "projectcfg-dependency-report-resources"

class DependencyReport constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        // add task and add it to the task graph
        val task = ctx.project.tasks.register(DEPENDENCY_REPORT_TASK_NAME, DependenciesReportResourcesTask::class.java) {
            it.fileName.set("dependencies.txt")
        }
        ctx.project.tasks.matching { it.name == "classes" }.configureEach {
            it.dependsOn(task)
            it.mustRunAfter("processResources")
        }
    }

    companion object {

    }
}