package com.github.philippheuer.gradleprojectsetup.policy

import com.github.philippheuer.gradleprojectsetup.EXTENSION_NAME
import com.github.philippheuer.gradleprojectsetup.ProjectSetupExtension
import com.github.philippheuer.gradleprojectsetup.domain.PluginModule
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

/**
 * Policy - this will ensure that a tested version of gradle is used with this plugin
 */
class GradleWrapperVersionPolicy constructor(override var project: Project, override var config: ProjectSetupExtension) : PluginModule {
    var allowedVersions = listOf("7.3")

    override fun check(): Boolean {
        log(LogLevel.DEBUG, "module check [$EXTENSION_NAME.gradleVersionCheckBypass] is [${config.gradleVersionCheckBypass.get()}]")
        return !config.gradleVersionCheckBypass.get()
    }

    override fun run() {
        project.run {
            if (!allowedVersions.contains(gradle.gradleVersion)) {
                // TODO: update the wrapper to the latest supported version instead of a error?
                log(LogLevel.ERROR, "checking [gradle.version] is [${gradle.gradleVersion}] result [not valid - one of $allowedVersions]")
                throw GradleException("checking [gradle.version] is [${gradle.gradleVersion}] result [not valid - one of $allowedVersions]")
            } else {
                log(LogLevel.INFO, "checking [gradle.version] is [${gradle.gradleVersion}] result [valid]")
            }
        }
    }
}