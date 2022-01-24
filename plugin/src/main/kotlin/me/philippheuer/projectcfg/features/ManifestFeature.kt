package me.philippheuer.projectcfg.features

import com.coditory.gradle.manifest.ManifestPluginExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyPlugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.jvm.tasks.Jar
import java.io.File

class ManifestFeature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return isProjectLanguage(ProjectLanguage.JAVA)
    }

    override fun run() {
        configurePlugin(ctx.project)
    }

    companion object {
        fun configurePlugin(project: Project) {
            project.applyPlugin("com.coditory.manifest")

            project.extensions.configure(ManifestPluginExtension::class.java) {
                it.buildAttributes = false
                PluginLogger.log(LogLevel.INFO, "set [manifest.buildAttributes] to [${it.buildAttributes}]")
            }

            project.tasks.withType(Jar::class.java).configureEach {
                it.dependsOn(project.tasks.getByName("manifest"))
                it.manifest.from(File(project.buildDir, "resources/main/META-INF/MANIFEST.MF"))
            }
        }
    }
}