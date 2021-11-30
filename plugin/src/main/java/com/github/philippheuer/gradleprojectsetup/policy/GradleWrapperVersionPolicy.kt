package com.github.philippheuer.gradleprojectsetup.policy

import com.github.philippheuer.gradleprojectsetup.ProjectSetupExtension
import com.github.philippheuer.gradleprojectsetup.domain.PluginModule
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

class GradleWrapperVersionPolicy constructor(override var project: Project, override var config: ProjectSetupExtension) : PluginModule {
    var allowedVersions = listOf("7.3")

    override fun check(): Boolean {
        return true
    }

    override fun run() {
        project.run {
            if (!allowedVersions.contains(gradle.gradleVersion)) {
                // TODO: update the wrapper to the latest supported version instead of a error?
                throw GradleException("gradle version ${gradle.gradleVersion} has not been tested! Please use one of the following versions $allowedVersions")
            } else {
                log(LogLevel.INFO, "using a tested gradle version ${gradle.gradleVersion}")
            }
        }
    }
}