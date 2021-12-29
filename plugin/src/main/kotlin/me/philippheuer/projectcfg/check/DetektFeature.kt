package me.philippheuer.projectcfg.check

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.applyProject
import org.gradle.api.Project

class DetektFeature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    companion object {
        fun applyPlugin(project: Project, config: ProjectConfigurationExtension) {
            project.applyProject("io.gitlab.arturbosch.detekt")
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

    override fun check(): Boolean {
        return config.language.get() == ProjectLanguage.KOTLIN
    }

    override fun run() {
        applyPlugin(project, config)
        applyConfiguration(project, config)
        applyReporting(project)
    }
}