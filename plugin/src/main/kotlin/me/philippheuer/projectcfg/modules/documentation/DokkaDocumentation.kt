package me.philippheuer.projectcfg.modules.documentation

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.JavadocIOUtils
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyPlugin
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
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
        return ctx.isProjectLanguage(ProjectLanguage.KOTLIN) && ctx.isProjectType(ProjectType.LIBRARY)
    }

    override fun run() {
        // javadoc task
        if (ctx.project.isRootProjectWithoutSubprojectsOrSubproject()) {
            configureDokkaTask(ctx)
        }
    }

    companion object {
        fun configureDokkaTask(ctx: IProjectContext) {
            ctx.project.applyPlugin("org.jetbrains.dokka")

            ctx.project.run {
                tasks.withType(Javadoc::class.java).configureEach {
                    it.enabled = false
                }

                tasks.named("javadocJar", Jar::class.java).configure {
                    it.from(tasks.named("dokkaJavadoc", DokkaTask::class.java))
                }

                tasks.named("dokkaJavadoc", DokkaTask::class.java).configure {
                    PluginLogger.setContext(ctx.project,"${DokkaDocumentation::class.java}")

                    it.moduleName.set("${project.rootProject.name} (${ctx.config.artifactVersion.get()}) - ${project.name}")
                    PluginLogger.log(LogLevel.INFO, "set [tasks.dokkaJavadoc.options.moduleName] to [${it.moduleName.get()}]")

                    it.dokkaSourceSets.configureEach { dss ->
                        dss.jdkVersion.set(ctx.config.javaVersionAsNumber())

                        // links
                        if (ctx.config.javadocLinks.get().size > 0) {
                            PluginLogger.log(LogLevel.INFO, "set [tasks.dokkaJavadoc.options.links] to [${ctx.config.javadocLinks.get()}]")
                            ctx.config.javadocLinks.get().forEach { link -> dss.externalDocumentationLink(link) }
                        }

                        // javadoc auto-linking via javadoc.io
                        if (ctx.config.javadocAutoLinking.get()) {
                            DependencyUtils.getDependencies(it.project, listOf("implementation", "api", "default")).forEach { dep ->
                                if (dep.version != null) {
                                    JavadocIOUtils.getLinkForDependency(project, dep.group, dep.name, dep.version)?.let {link ->
                                        PluginLogger.log(LogLevel.DEBUG, "append [tasks.dokkaJavadoc.options.links] element [${link}]")
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
