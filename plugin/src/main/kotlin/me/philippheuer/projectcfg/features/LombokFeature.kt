package me.philippheuer.projectcfg.features

import io.freefair.gradle.plugins.lombok.LombokExtension
import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyProject
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.javadoc.Javadoc

class LombokFeature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        if (ctx.project.isRootProjectWithoutSubprojectsOrSubproject()) {
            return isProjectLanguage(ProjectLanguage.JAVA)
        }

        return false
    }

    override fun run() {
        configurePlugin(ctx.project, ctx.config)
        if (ctx.config.javadocLombok.get()) {
            PluginLogger.log(LogLevel.INFO, "option [javadocLombok] is [${ctx.config.javadocLombok.get()}]")
            configureJavadoc(ctx.project)
        }
    }

    companion object {
        fun configurePlugin(project: Project, config: ProjectConfigurationExtension) {
            project.applyProject("io.freefair.lombok")

            project.extensions.configure(LombokExtension::class.java) {
                it.disableConfig.set(true) // don't generate lombok.config files
                PluginLogger.log(LogLevel.INFO, "set [lombok.disableConfig] to [${it.disableConfig.get()}]")
                it.version.set(config.lombokVersion.get())
                PluginLogger.log(LogLevel.INFO, "set [lombok.version] to [${it.version.get()}]")
            }
        }

        fun configureJavadoc(project: Project) {
            // javadoc - delombok
            val delombok = project.tasks.getByName("delombok")
            project.tasks.withType(Javadoc::class.java).configureEach {
                PluginLogger.log(LogLevel.INFO, "set [tasks.javadoc.source] to [delombok]")
                it.source(delombok)
                PluginLogger.log(LogLevel.INFO, "set [tasks.javadoc.dependsOn] to [delombok]")
                it.dependsOn(delombok)
            }
        }
    }
}