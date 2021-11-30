package com.github.philippheuer.gradleprojectsetup.features

import com.github.philippheuer.gradleprojectsetup.ProjectSetupExtension
import com.github.philippheuer.gradleprojectsetup.domain.PluginModule
import com.github.philippheuer.gradleprojectsetup.domain.ProjectLanguage
import io.freefair.gradle.plugins.lombok.LombokExtension
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ObjectConfigurationAction

class LombokFeature constructor(override var project: Project, override var config: ProjectSetupExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectLanguage.JAVA == config.language.get()
    }

    override fun run() {
        project.run {
            apply { action: ObjectConfigurationAction ->
                log(LogLevel.INFO, "applying gradle plugin [io.freefair.lombok]")
                action.plugin("io.freefair.lombok")
            }

            extensions.run {
                configure(LombokExtension::class.java) {
                    it.disableConfig.set(true) // don't generate lombok.config files
                    it.version.set("1.18.22")
                }
            }
        }
    }
}