package me.philippheuer.projectcfg.modules.features

import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
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
            project.applyPlugin("com.github.johnrengelman.shadow")
        }

        fun configurePlugin(project: Project, config: ProjectConfigurationExtension) {
            if (config.shadowRelocate.isPresent) {
                val relocateTask = project.tasks.create("relocateShadowJar", ConfigureShadowRelocation::class.java) {
                    it.target = project.tasks.named("shadowJar", ShadowJar::class.java).get()
                    it.prefix = config.shadowRelocate.get()
                }

                project.tasks.withType(Jar::class.java).configureEach {
                    if (it is ShadowJar) {
                        it.dependsOn(relocateTask)
                        it.archiveClassifier.set("shaded")
                    }
                }
            }
        }
    }
}