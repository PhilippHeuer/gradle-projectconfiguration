package me.philippheuer.projectcfg.modules.features

import com.coditory.gradle.manifest.ManifestPluginExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginHelper
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyPlugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.jvm.tasks.Jar
import java.io.File

class ManifestFeature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        // manifest plugin is causing build cache issues, only enable when running publish
        return ctx.isProjectLanguage(ProjectLanguage.JAVA) && !ctx.project.pluginManager.hasPlugin("java-platform") && ctx.project.gradle.startParameter.taskNames.any { s -> s.startsWith("publish") }
    }

    override fun run() {
        configurePlugin(ctx.project)
    }

    companion object {
        fun configurePlugin(project: Project) {
            project.applyPlugin("com.coditory.manifest")

            // disable buildAttributes, unless the build is running in CI
            if (!PluginHelper.isCI()) {
                project.project.extensions.getByType(ManifestPluginExtension::class.java).apply {
                    buildAttributes = false
                    PluginLogger.log(LogLevel.INFO, "set [manifest.buildAttributes] to [$buildAttributes]")
                }
            }

            // jar depends on manifest
            project.tasks.withType(Jar::class.java).configureEach {
                it.dependsOn(project.tasks.getByName("manifest"))
                it.manifest.from(File(project.buildDir, "resources/main/META-INF/MANIFEST.MF"))
            }
        }
    }
}
