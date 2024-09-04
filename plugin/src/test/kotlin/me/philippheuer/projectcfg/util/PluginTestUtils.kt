package me.philippheuer.projectcfg.util

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.ProjectLanguage
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.testfixtures.ProjectBuilder

class PluginTestUtils {

    companion object {
        fun getJavaProject(): Pair<Project, ProjectConfigurationExtension> {
            val project: Project = ProjectBuilder.builder().build()
            project.pluginManager.apply("java")
            val config = ProjectConfigurationExtension(project)
            config.logLevel.set(LogLevel.INFO)
            config.language.set(ProjectLanguage.JAVA)

            PluginLogger.project = project
            PluginLogger.setLogLevel(LogLevel.INFO)

            return Pair(project, config)
        }
    }

}
