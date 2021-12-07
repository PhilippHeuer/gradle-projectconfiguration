package io.github.philippheuer.gradleprojectsetup

import io.github.philippheuer.gradleprojectsetup.features.*
import io.github.philippheuer.gradleprojectsetup.framework.QuarkusFramework
import io.github.philippheuer.gradleprojectsetup.framework.SpringBootFramework
import io.github.philippheuer.gradleprojectsetup.policy.GradleWrapperVersionPolicy
import io.github.philippheuer.gradleprojectsetup.type.JavaLibraryType
import io.github.philippheuer.gradleprojectsetup.type.JavaType
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

const val EXTENSION_NAME = "projectSetup"

abstract class ProjectSetupPlugin : Plugin<Project> {
    companion object {
        private val log = LoggerFactory.getLogger(ProjectSetupPlugin::class.java)
    }

    override fun apply(project: Project) {
        val config = project.extensions.create(EXTENSION_NAME, io.github.philippheuer.gradleprojectsetup.ProjectSetupExtension::class.java)

        project.afterEvaluate { // TODO: config property values are only accessible in afterEvaluate, but there should be a better way maybe?
            // process each module
            val modules = listOf(
                // type
                JavaType(project, config),
                JavaLibraryType(project, config),
                // frameworks
                SpringBootFramework(project, config),
                QuarkusFramework(project, config),
                // features
                LombokFeature(project, config),
                TestLoggingFeature(project, config),
                JavadocFeature(project, config),
                ShadowFeature(project, config),
                ManifestFeature(project, config),
                JUnit5Feature(project, config),
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
    }

}
