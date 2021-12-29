package me.philippheuer.projectcfg

import me.philippheuer.projectcfg.check.CheckstyleFeature
import me.philippheuer.projectcfg.check.DetektFeature
import me.philippheuer.projectcfg.cve.Log4JCVE
import me.philippheuer.projectcfg.features.*
import me.philippheuer.projectcfg.framework.QuarkusFramework
import me.philippheuer.projectcfg.framework.SpringBootFramework
import me.philippheuer.projectcfg.policy.GradleWrapperVersionPolicy
import me.philippheuer.projectcfg.type.JavaLibraryType
import me.philippheuer.projectcfg.type.JavaApplicationType
import me.philippheuer.projectcfg.util.PluginLogger
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

        // logger
        PluginLogger.project = project
        PluginLogger.config = config

        // process features
        project.afterEvaluate { // TODO: config property values are only accessible in afterEvaluate, but there should be a better way maybe?
            // config preprocessing
            config.defaults()

            // process each module
            val modules = listOf(
                // policy
                GradleWrapperVersionPolicy(project, config),
                // cve
                Log4JCVE(project, config),
                // type
                JavaApplicationType(project, config),
                JavaLibraryType(project, config),
                // frameworks
                SpringBootFramework(project, config),
                QuarkusFramework(project, config),
                // features
                PublishFeature(project, config),
                SigningFeature(project, config),
                LombokFeature(project, config),
                TestLoggingFeature(project, config),
                JavadocFeature(project, config),
                MockitoFeature(project, config),
                ShadowFeature(project, config),
                ManifestFeature(project, config),
                JUnit5Feature(project, config),
                VersionUpgradeFeature(project, config),
                // check
                CheckstyleFeature(project, config),
                DetektFeature(project, config),
            )

            modules.forEach {
                PluginLogger.module = it
                val enabled = it.check()
                if (enabled) {
                    it.run()
                }
            }
        }
    }

}
