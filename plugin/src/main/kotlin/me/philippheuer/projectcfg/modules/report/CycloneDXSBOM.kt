package me.philippheuer.projectcfg.modules.report

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import org.cyclonedx.gradle.CycloneDxTask

class CycloneDXSBOM constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        ctx.project.tasks.withType(CycloneDxTask::class.java).configureEach {
            it.includeConfigs.set(listOf("runtimeClasspath"))
            it.skipConfigs.set(listOf("compileClasspath", "testCompileClasspath"))

            if (ctx.isProjectType(ProjectType.LIBRARY)) {
                it.projectType.set("library")
            } else {
                it.projectType.set("application")
            }

            it.schemaVersion.set("1.4")
            it.outputName.set("bom")
            it.outputFormat.set("all")
            it.includeBomSerialNumber.set(false)
        }
    }

    companion object {

    }
}
