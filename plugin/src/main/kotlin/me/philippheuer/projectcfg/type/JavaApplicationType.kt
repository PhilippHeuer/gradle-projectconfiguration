package me.philippheuer.projectcfg.type

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyProject
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.api.tasks.compile.JavaCompile

class JavaApplicationType constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectLanguage.JAVA == config.language.get() && ProjectType.APP == config.type.get()
    }

    override fun run() {
        configureJavaApplication(project, config)
    }

    fun configureJavaApplication(project: Project, config: ProjectConfigurationExtension) {
        project.applyProject("java")

        project.run {
            group = config.artifactGroupId.get()
            version = config.artifactVersion.get()

            extensions.run {
                configure(BasePluginExtension::class.java) {
                    it.archivesName.set(config.artifactId.get())
                }

                configure(JavaPluginExtension::class.java) {
                    // java version
                    it.sourceCompatibility = config.javaVersion.get()
                    it.targetCompatibility = config.javaVersion.get()
                }
            }

            tasks.withType(JavaCompile::class.java).configureEach {
                it.options.encoding = config.fileEncoding.get()
            }
        }
    }
}
