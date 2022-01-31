package me.philippheuer.projectcfg.modules.report.tasks

import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.TaskUtils
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class DependenciesReportResourcesTask : DefaultTask() {

    init {
        group = "projectcfg"
        description = "embeds a dependencies.txt into the resources, which is a list of all used dependencies + version"
    }

    @get:Input
    @get:Option(option = "fileName", description = "the fileName to create in the resources dir")
    abstract val fileName: Property<String>

    @TaskAction
    fun generateDependencyReportAction() {
        val dependencyReport = generateDependencyReport()

        TaskUtils.writeContentToOutputResources(project, fileName.get(), dependencyReport)
    }

    private fun generateDependencyReport(): String {
        val deps = DependencyUtils.getResolvedDependencies(project, listOf("compileClasspath"))

        val content = StringBuilder()
        deps.forEach { content.appendLine(it) }
        return content.toString()
    }
}