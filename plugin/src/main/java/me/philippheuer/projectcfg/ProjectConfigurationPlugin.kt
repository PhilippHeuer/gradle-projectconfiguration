package me.philippheuer.projectcfg

import me.philippheuer.projectcfg.features.*
import me.philippheuer.projectcfg.framework.QuarkusFramework
import me.philippheuer.projectcfg.framework.SpringBootFramework
import me.philippheuer.projectcfg.policy.GradleWrapperVersionPolicy
import me.philippheuer.projectcfg.type.JavaLibraryType
import me.philippheuer.projectcfg.type.JavaApplicationType
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

const val EXTENSION_NAME = "projectConfiguration"

abstract class ProjectConfigurationPlugin : Plugin<Project> {
    companion object {
        private val log = LoggerFactory.getLogger(ProjectConfigurationPlugin::class.java)
    }

    override fun apply(project: Project) {
        val config = project.extensions.create(EXTENSION_NAME, ProjectConfigurationExtension::class.java)

        project.afterEvaluate { // TODO: config property values are only accessible in afterEvaluate, but there should be a better way maybe?
            // process each module
            val modules = listOf(
                // policy
                GradleWrapperVersionPolicy(project, config),
                // type
                JavaApplicationType(project, config),
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
                JUnit5Feature(project, config)
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
