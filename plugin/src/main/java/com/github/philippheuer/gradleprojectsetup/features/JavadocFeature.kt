package com.github.philippheuer.gradleprojectsetup.features

import com.github.philippheuer.gradleprojectsetup.ProjectSetupExtension
import com.github.philippheuer.gradleprojectsetup.domain.PluginModule
import com.github.philippheuer.gradleprojectsetup.domain.ProjectLanguage
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions

class JavadocFeature constructor(override var project: Project, override var config: ProjectSetupExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectLanguage.JAVA == config.language.get()
    }

    override fun run() {
        project.run {
            // global options
            tasks.withType(Javadoc::class.java).forEach {
                it.options.windowTitle = "${project.rootProject.name} (v${project.version})"
                log(LogLevel.INFO, "set [tasks.javadoc.options.windowTitle] to [${it.options.windowTitle}]")
                it.options.encoding = "UTF-8"
                log(LogLevel.INFO, "set [tasks.javadoc.options.encoding] to [${it.options.encoding}]")
            }

            // html5 for jdk9+
            log(LogLevel.DEBUG, "using jdk9 or later, javadoc supports html5 output")
            if (JavaVersion.current().isJava9Compatible) {
                log(LogLevel.INFO, "set [tasks.javadoc.options.html5] to [true]")
                tasks.withType(Javadoc::class.java).forEach {
                    (it.options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
                }
            }
        }
    }
}
