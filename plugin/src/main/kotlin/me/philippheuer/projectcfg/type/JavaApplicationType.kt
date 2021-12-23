package me.philippheuer.projectcfg.type

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
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
        project.run {
            log(LogLevel.INFO, "applying plugin [java]")
            pluginManager.apply("java")

            extensions.run {
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
