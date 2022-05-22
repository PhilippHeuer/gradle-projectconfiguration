package me.philippheuer.projectcfg

import me.philippheuer.projectcfg.domain.ProjectContext
import me.philippheuer.projectcfg.modules.check.CheckstyleFeature
import me.philippheuer.projectcfg.modules.check.DetektFeature
import me.philippheuer.projectcfg.modules.documentation.DokkaDocumentation
import me.philippheuer.projectcfg.modules.documentation.JavadocDocumentation
import me.philippheuer.projectcfg.modules.features.GitPropertiesFeature
import me.philippheuer.projectcfg.modules.features.JUnit5Feature
import me.philippheuer.projectcfg.modules.features.LoggingLibraryFeature
import me.philippheuer.projectcfg.modules.features.LombokFeature
import me.philippheuer.projectcfg.modules.features.ManifestFeature
import me.philippheuer.projectcfg.modules.features.PublishFeature
import me.philippheuer.projectcfg.modules.features.ShadowFeature
import me.philippheuer.projectcfg.modules.features.SigningFeature
import me.philippheuer.projectcfg.modules.features.TestLoggingFeature
import me.philippheuer.projectcfg.modules.features.VersionUpgradeFeature
import me.philippheuer.projectcfg.modules.framework.QuarkusFramework
import me.philippheuer.projectcfg.modules.framework.SpringBootFramework
import me.philippheuer.projectcfg.modules.policy.GradleWrapperVersionPolicy
import me.philippheuer.projectcfg.modules.report.DependencyReport
import me.philippheuer.projectcfg.modules.type.JavaApplicationType
import me.philippheuer.projectcfg.modules.type.JavaLibraryType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
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
        val ctx = ProjectContext(project, config)

        // logger
        PluginLogger.project = project
        PluginLogger.config = config

        // process each module
        val modules = listOf(
            // policy
            GradleWrapperVersionPolicy(ctx),
            // type
            JavaApplicationType(ctx),
            JavaLibraryType(ctx),
            // frameworks
            SpringBootFramework(ctx),
            QuarkusFramework(ctx),
            // documentation
            DokkaDocumentation(ctx),
            JavadocDocumentation(ctx),
            // features
            PublishFeature(ctx),
            SigningFeature(ctx),
            LombokFeature(ctx),
            TestLoggingFeature(ctx),
            ShadowFeature(ctx),
            ManifestFeature(ctx),
            JUnit5Feature(ctx),
            VersionUpgradeFeature(ctx),
            LoggingLibraryFeature(ctx),
            GitPropertiesFeature(ctx),
            // check
            CheckstyleFeature(ctx),
            DetektFeature(ctx),
            // reporting
            DependencyReport(ctx),
        )

        // init module
        modules.forEach {
            PluginLogger.setContext(project, config, "${it::class.java}")
            it.init()
        }

        // process features
        project.afterEvaluate { // TODO: config property values are only accessible in afterEvaluate, but there should be a better way maybe?
            // config preprocessing
            config.defaults()

            // process module
            modules.forEach {
                PluginLogger.setContext(project, config, "${it::class.java}")
                val enabled = it.check()
                if (enabled) {
                    if (project.isRootProjectWithoutSubprojectsOrSubproject()) {
                        it.run()
                    }
                }
            }
        }
    }
}
