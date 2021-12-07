package io.github.philippheuer.gradleprojectsetup.type

import io.github.philippheuer.gradleprojectsetup.ProjectSetupExtension
import io.github.philippheuer.gradleprojectsetup.domain.PluginModule
import io.github.philippheuer.gradleprojectsetup.domain.ProjectLanguage
import io.github.philippheuer.gradleprojectsetup.domain.ProjectType
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.api.tasks.compile.JavaCompile

class JavaType constructor(override var project: Project, override var config: ProjectSetupExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectLanguage.JAVA == config.language.get() && ProjectType.APP == config.type.get()
    }

    override fun run() {
        project.run {
            apply { action: ObjectConfigurationAction ->
                log(LogLevel.INFO, "applying plugin [java]")
                action.plugin("java")
            }

            extensions.run {
                configure(JavaPluginExtension::class.java) {
                    // java version
                    it.sourceCompatibility = config.javaVersion.get()
                    it.sourceCompatibility = config.javaVersion.get()
                }
            }

            tasks.withType(JavaCompile::class.java).configureEach {
                it.options.encoding = "UTF-8"
            }
        }
    }
}
