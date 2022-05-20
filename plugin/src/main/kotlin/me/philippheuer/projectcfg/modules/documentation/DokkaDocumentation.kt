package me.philippheuer.projectcfg.modules.documentation

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.JavadocIOUtils
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyPlugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask

/**
 * Dokka Module
 *
 * @param ctx plugin context
 * @constructor Creates a new instance of this module
 */
class DokkaDocumentation constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.isProjectLanguage(ProjectLanguage.KOTLIN)
    }

    override fun run() {
        // javadoc task
        if (ctx.project.subprojects.size == 0 || (ctx.project.subprojects.size != 0 && ctx.project.rootProject != ctx.project)) {
            configureDokkaTask(ctx.project, ctx.config)
        }
    }

    companion object {
        fun configureDokkaTask(project: Project, config: ProjectConfigurationExtension) {
            project.applyPlugin("org.jetbrains.dokka")

            project.run {
                tasks.withType(Javadoc::class.java).configureEach {
                    it.enabled = false
                }

                tasks.named("javadocJar", Jar::class.java).configure {
                    it.from(tasks.named("dokkaJavadoc", DokkaTask::class.java))
                }

                tasks.named("dokkaJavadoc", DokkaTask::class.java).configure {
                    it.moduleName.set("${project.rootProject.name} (v${project.version}) - ${project.name}")
                    PluginLogger.log(LogLevel.INFO, "set [tasks.dokkaJavadoc.options.moduleName] to [${it.moduleName.get()}]")

                    it.dokkaSourceSets.configureEach { dss ->
                        dss.jdkVersion.set(config.javaVersionAsNumber())

                        // links
                        if (config.javadocLinks.get().size > 0) {
                            PluginLogger.log(LogLevel.INFO, "set [tasks.javadoc.options.links] to [${config.javadocLinks.get()}]")
                            config.javadocLinks.get().forEach { link -> dss.externalDocumentationLink(link) }
                        }

                        // javadoc auto-linking via javadoc.io
                        if (config.javadocAutoLinking.get()) {
                            DependencyUtils.getDependencies(it.project, listOf("implementation", "api", "default")).forEach { dep ->
                                if (dep.version != null) {
                                    JavadocIOUtils.getLinkForDependency(project, dep.group, dep.name, dep.version)?.let {link ->
                                        PluginLogger.log(LogLevel.DEBUG, "append [tasks.javadoc.options.links] element [${link}]")
                                        dss.externalDocumentationLink(link)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
