package me.philippheuer.projectcfg.modules.features

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.applyPlugin
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

class ShadowFeature(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        if (ctx.project.isRootProjectWithoutSubprojectsOrSubproject() && ctx.isProjectLanguage(ProjectLanguage.JAVA) && !ctx.project.pluginManager.hasPlugin("java-platform") && !ctx.project.pluginManager.hasPlugin("version-catalog") && ctx.config.shadow.get()) {
            return true
        }

        return false
    }

    override fun run() {
        applyPlugin(ctx.project)
        configurePlugin(ctx.project, ctx.config)
    }

    companion object {
        fun applyPlugin(project: Project) {
            project.applyPlugin("com.gradleup.shadow")
        }

        fun configurePlugin(project: Project, config: ProjectConfigurationExtension) {
            project.tasks.withType(Jar::class.java) {
                if (it !is ShadowJar) {
                    return@withType
                }

                it.archiveClassifier.set("shaded")

                if (config.shadowRelocate.isPresent) {
                    it.enableAutoRelocation.set(true)
                    it.relocationPrefix.set(config.shadowRelocate.get())
                }
            }
        }
    }
}
