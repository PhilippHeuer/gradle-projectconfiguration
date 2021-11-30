package com.github.philippheuer.gradleprojectsetup

import com.github.philippheuer.gradleprojectsetup.features.LombokFeature
import com.github.philippheuer.gradleprojectsetup.features.TestLoggingFeature
import com.github.philippheuer.gradleprojectsetup.framework.QuarkusFramework
import com.github.philippheuer.gradleprojectsetup.framework.SpringBootFramework
import com.github.philippheuer.gradleprojectsetup.policy.GradleWrapperVersionPolicy
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

const val EXTENSION_NAME = "projectSetup"

abstract class ProjectSetupPlugin : Plugin<Project> {
    companion object {
        private val log = LoggerFactory.getLogger(ProjectSetupPlugin::class.java)
    }

    override fun apply(project: Project) {
        val config = project.extensions.create(EXTENSION_NAME, ProjectSetupExtension::class.java)

        project.afterEvaluate { // TODO: config property values are only accessible in afterEvaluate, but there should be a better way maybe?
            // process each module
            val modules = listOf(
                // frameworks
                SpringBootFramework(project, config),
                QuarkusFramework(project, config),
                // features
                LombokFeature(project, config),
                TestLoggingFeature(project, config),
                // policy
                GradleWrapperVersionPolicy(project, config)
            )

            modules.forEach {
                val enabled = it.check()
                if (enabled) {
                    it.run()
                }
            }
        }

        // example task
        project.tasks.register("example-task", ProjectSetupExampleTask::class.java) {
            // ...
        }
    }

}
