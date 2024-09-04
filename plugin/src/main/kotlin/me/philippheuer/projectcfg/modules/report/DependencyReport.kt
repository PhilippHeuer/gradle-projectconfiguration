package me.philippheuer.projectcfg.modules.report

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.modules.report.tasks.DependenciesReportResourcesTask

private const val DEPENDENCY_REPORT_TASK_NAME = "projectcfg-dependency-report-resources"

class DependencyReport(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        // check if task already exists
        if (ctx.project.tasks.findByName(DEPENDENCY_REPORT_TASK_NAME) != null) {
            return
        }

        // add task and add it to the task graph
        val task = ctx.project.tasks.register(DEPENDENCY_REPORT_TASK_NAME, DependenciesReportResourcesTask::class.java) {
            it.fileName.set("dependencies.txt")
        }
        ctx.project.tasks.matching { it.name == "classes" }.configureEach {
            it.dependsOn(task)
            it.mustRunAfter("processResources")
        }
    }
}