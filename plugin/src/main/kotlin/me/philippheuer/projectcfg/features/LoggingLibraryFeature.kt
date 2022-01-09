package me.philippheuer.projectcfg.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.addDepdenency
import org.gradle.api.Project

class LoggingLibraryFeature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        var configurationName = "implementation"
        if (ProjectType.LIBRARY.valueEquals(config.type.get())) {
            configurationName = "api"
        }

        // java
        if (ProjectLanguage.JAVA.valueEquals(config.language.get())) {
            project.addDepdenency(configurationName, "org.slf4j:slf4j-api:1.7.32")
        }

        // kotlin
        if (ProjectLanguage.KOTLIN.valueEquals(config.language.get())) {
            project.addDepdenency(configurationName, "io.github.microutils:kotlin-logging:${DependencyVersion.kotlinLoggingVersion}")
        }
    }
}