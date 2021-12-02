package com.github.philippheuer.gradleprojectsetup.features

import com.github.philippheuer.gradleprojectsetup.ProjectSetupExtension
import com.github.philippheuer.gradleprojectsetup.domain.PluginModule
import com.github.philippheuer.gradleprojectsetup.domain.ProjectLanguage
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.compile.JavaCompile

class UTF8Feature constructor(override var project: Project, override var config: ProjectSetupExtension) :
    PluginModule {
    override fun check(): Boolean {
        return ProjectLanguage.JAVA == config.language.get()
    }

    override fun run() {
        project.run {
            log(LogLevel.INFO, "set [tasks.JavaCompile.encoding] to [UTF-8]")

            tasks.withType(JavaCompile::class.java).forEach {
                it.options.encoding = "UTF-8"
            }
        }
    }
}
