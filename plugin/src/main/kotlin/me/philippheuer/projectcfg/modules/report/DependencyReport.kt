package me.philippheuer.projectcfg.modules.report

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.modules.report.tasks.DependenciesReportResourcesTask
import me.philippheuer.projectcfg.util.DependencyUtils

private const val DEPENDENCY_REPORT_TASK_NAME = "projectcfg-dependency-report-resources"
private const val DEPENDENCY_REPORT_OUTPUT_DIR = "generated/depreport/resources"

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
            val deps = DependencyUtils.getResolvedDependencies(ctx.project, listOf("compileClasspath"))

            it.outputFile.set(ctx.project.layout.buildDirectory.file("$DEPENDENCY_REPORT_OUTPUT_DIR/dependencies.txt").get().asFile)
            it.dependencyList.set(deps)
        }
        ctx.project.extensions.getByName("sourceSets")
            .let { it as org.gradle.api.tasks.SourceSetContainer }
            .getByName("main")
            .resources.srcDir(ctx.project.layout.buildDirectory.dir(DEPENDENCY_REPORT_OUTPUT_DIR))

        ctx.project.tasks.named("processResources").configure {
            it.dependsOn(task)
        }
        ctx.project.tasks.matching { it.name == "classes" || it.name == "sourcesJar" }.configureEach {
            it.dependsOn(task)
            it.mustRunAfter("processResources")
        }
    }
}
