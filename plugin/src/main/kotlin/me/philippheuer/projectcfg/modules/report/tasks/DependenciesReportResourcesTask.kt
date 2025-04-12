package me.philippheuer.projectcfg.modules.report.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@CacheableTask
abstract class DependenciesReportResourcesTask : DefaultTask() {

    @get:Input
    abstract val dependencyList: ListProperty<String>

    @get:Option(option = "outputFile", description = "the output file name")
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = "projectcfg"
        description = "embeds a dependencies.txt into the resources, which is a list of all used dependencies + version"
    }

    @TaskAction
    fun generateDependencyReportAction() {
        val content = dependencyList.get().sorted().joinToString("\n")
        outputFile.get().asFile.parentFile.mkdirs()
        outputFile.get().asFile.writeText(content)
    }
}
