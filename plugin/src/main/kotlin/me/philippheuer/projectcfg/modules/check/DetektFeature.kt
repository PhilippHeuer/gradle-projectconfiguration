package me.philippheuer.projectcfg.modules.check

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.applyPlugin
import org.gradle.api.Project

class DetektFeature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.isProjectLanguage(ProjectLanguage.KOTLIN)
    }

    override fun run() {
        applyPlugin(ctx.project)
        applyConfiguration(ctx.project, ctx.config)
        applyReporting(ctx.project)
    }

    companion object {
        fun applyPlugin(project: Project) {
            project.applyPlugin("io.gitlab.arturbosch.detekt")
        }

        fun applyConfiguration(project: Project, config: ProjectConfigurationExtension) {
            project.extensions.configure(DetektExtension::class.java) {
                it.buildUponDefaultConfig = true // preconfigure defaults
                it.allRules = false // activate all available (even unstable) rules.
            }

            // jvm target
            project.tasks.withType(Detekt::class.java).configureEach {
                it.jvmTarget = config.javaVersionAsJvmVersion()
            }
            project.tasks.withType(DetektCreateBaselineTask::class.java).configureEach {
                it.jvmTarget = config.javaVersionAsJvmVersion()
            }
        }

        fun applyReporting(project: Project) {
            project.tasks.withType(Detekt::class.java).configureEach {
                it.reports.html.required.set(true)
                it.reports.xml.required.set(true)
            }
        }
    }
}