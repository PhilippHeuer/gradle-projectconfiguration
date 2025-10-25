package me.philippheuer.projectcfg.modules.report

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.modules.report.tasks.DependenciesReportResourcesTask
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.logging.LogLevel

private const val DEPENDENCY_REPORT_TASK_NAME = "projectcfg-dependency-report-resources"
private const val DEPENDENCY_REPORT_OUTPUT_DIR = "generated/depreport/resources"

class DependencyReport(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        if (!ctx.project.isRootProjectWithoutSubprojectsOrSubproject()) {
            return false
        }
        if (ctx.project.pluginManager.hasPlugin("java-platform") || ctx.project.pluginManager.hasPlugin("version-catalog")) {
            return false
        }

        return true
    }

    override fun run() {
        // check if task already exists
        if (ctx.project.tasks.findByName(DEPENDENCY_REPORT_TASK_NAME) != null) {
            return
        }

        // check if sourceSets
        val sourceSets = ctx.project.extensions.getByType(org.gradle.api.tasks.SourceSetContainer::class.java)
        if (sourceSets.findByName("main") == null) {
            PluginLogger.log(LogLevel.DEBUG, "No 'main' sourceSet found in project ${ctx.project.name}, skipping dependency report setup.")
            return
        }

        // add task and add it to the task graph
        PluginLogger.log(LogLevel.INFO, "Adding task ${DEPENDENCY_REPORT_TASK_NAME} to project ${ctx.project.name}")
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
