package me.philippheuer.projectcfg.modules.report

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import org.cyclonedx.gradle.CycloneDxTask
import org.cyclonedx.model.Component
import org.cyclonedx.Version

class CycloneDXSBOM(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        ctx.project.tasks.withType(CycloneDxTask::class.java).configureEach {
            it.includeConfigs.set(listOf("runtimeClasspath", "compileClasspath"))
            it.skipConfigs.set(listOf("testCompileClasspath", ".*test.*", ".*Test.*"))

            if (ctx.isProjectType(ProjectType.LIBRARY)) {
                it.projectType.set(Component.Type.LIBRARY)
            } else {
                it.projectType.set(Component.Type.APPLICATION)
            }

            it.schemaVersion.set(Version.VERSION_16)
            it.includeBomSerialNumber.set(false)
            it.includeBuildSystem.set(false)
        }
    }
}
