package me.philippheuer.projectcfg.features

import com.coditory.gradle.manifest.ManifestPluginExtension
import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyProject
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.jvm.tasks.Jar
import java.io.File

class ManifestFeature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectLanguage.JAVA == config.language.get()
    }

    override fun run() {
        configurePlugin(project)
    }

    fun configurePlugin(project: Project) {
        project.applyProject("com.coditory.manifest")

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