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
import me.philippheuer.projectcfg.util.toMajorVersion
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateModuleTask

/**
 * Dokka Module
 *
 * @param ctx plugin context
 * @constructor Creates a new instance of this module
 */
class DokkaDocumentation(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.isProjectLanguage(ProjectLanguage.KOTLIN) && ctx.isProjectType(ProjectType.LIBRARY)
    }

    override fun run() {
        // javadoc task
        if (ctx.project.isRootProjectWithoutSubprojectsOrSubproject()) {
            configureDokkaTaskV2(ctx)
        }
    }

    companion object {
        fun configureDokkaTaskV2(ctx: IProjectContext) {
            val project = ctx.project

            // temporary for dokka v2 migration
            project.extensions.extraProperties["org.jetbrains.dokka.experimental.gradle.pluginMode"] = "V2Enabled"
            project.extensions.extraProperties["org.jetbrains.dokka.experimental.gradle.pluginMode.noWarn"] = "true"

            project.applyPlugin("org.jetbrains.dokka")
            project.applyPlugin("org.jetbrains.dokka-javadoc")

            // replace javadoc contents with dokka output
            project.tasks.withType(Javadoc::class.java).configureEach {
                it.enabled = false
            }
            project.tasks.named("javadocJar", Jar::class.java).configure { jar ->
                val dokkaJavadocTask = project.tasks.named("dokkaGenerateModuleJavadoc", DokkaGenerateModuleTask::class.java)
                val dokkaOutputDir = dokkaJavadocTask.flatMap { it.outputDirectory }
                jar.from(dokkaOutputDir.map { dir -> dir.dir("module") })
            }

            project.extensions.configure(DokkaExtension::class.java) { dokka ->
                dokka.moduleName.set("${project.rootProject.name} (${ctx.config.artifactVersion.get()}) - ${project.name}")
                PluginLogger.log(LogLevel.INFO, "set [tasks.dokkaJavadoc.options.moduleName] to [${dokka.moduleName.get()}]")

                dokka.dokkaSourceSets.configureEach { dss ->
                    dss.jdkVersion.set(ctx.config.javaVersion.map { jv -> jv.toMajorVersion() }.get())

                    // links
                    if (ctx.config.javadocLinks.get().isNotEmpty()) {
                        PluginLogger.log(LogLevel.INFO, "set [tasks.dokkaJavadoc.options.links] to [${ctx.config.javadocLinks.get()}]")
                        ctx.config.javadocLinks.get().forEach { link -> dss.externalDocumentationLinks.create(link) { it.url(link) } }
                    }

                    // javadoc auto-linking via javadoc.io
                    if (ctx.config.javadocAutoLinking.get()) {
                        DependencyUtils.getDependencies(project.project, listOf("implementation", "api", "default")).forEach { dep ->
                            if (dep.version != null) {
                                JavadocIOUtils.getLinkForDependency(project, dep.group, dep.name, dep.version)?.let { link ->
                                    PluginLogger.log(LogLevel.DEBUG, "append [tasks.dokkaJavadoc.options.links] element [${link}]")
                                    dss.externalDocumentationLinks.create(link) { it.url(link) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
