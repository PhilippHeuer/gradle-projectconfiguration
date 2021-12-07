package me.philippheuer.projectcfg.type

import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.api.tasks.compile.JavaCompile

class JavaLibraryType constructor(override var project: Project, override var config: me.philippheuer.projectcfg.ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectLanguage.JAVA == config.language.get() && ProjectType.LIBRARY == config.type.get()
    }

    override fun run() {
        project.run {
            apply { action: ObjectConfigurationAction ->
                log(LogLevel.INFO, "applying plugin [java-library]")
                action.plugin("java-library")
            }

            extensions.run {
                configure(JavaPluginExtension::class.java) {
                    // java version
                    it.sourceCompatibility = config.javaVersion.get()
                    it.sourceCompatibility = config.javaVersion.get()

                    // sources / javadocs
                    it.withSourcesJar()
                    it.withJavadocJar()
                }
            }

            tasks.withType(JavaCompile::class.java).configureEach {
                it.options.encoding = "UTF-8"
            }
        }
    }
}
