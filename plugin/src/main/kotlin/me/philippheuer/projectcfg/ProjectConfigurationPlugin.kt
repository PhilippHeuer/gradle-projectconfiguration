package me.philippheuer.projectcfg

import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectContext
import me.philippheuer.projectcfg.modules.check.CheckstyleFeature
import me.philippheuer.projectcfg.modules.check.DetektFeature
import me.philippheuer.projectcfg.modules.documentation.DokkaDocumentation
import me.philippheuer.projectcfg.modules.documentation.JavadocDocumentation
import me.philippheuer.projectcfg.modules.features.AutomaticModuleNameFeature
import me.philippheuer.projectcfg.modules.features.GitPropertiesFeature
import me.philippheuer.projectcfg.modules.features.JUnit5Feature
import me.philippheuer.projectcfg.modules.features.JacocoFeature
import me.philippheuer.projectcfg.modules.features.LoggingLibraryFeature
import me.philippheuer.projectcfg.modules.features.LombokFeature
import me.philippheuer.projectcfg.modules.features.PublishFeature
import me.philippheuer.projectcfg.modules.features.ReproducibleArchivesFeature
import me.philippheuer.projectcfg.modules.features.ShadowFeature
import me.philippheuer.projectcfg.modules.features.SigningFeature
import me.philippheuer.projectcfg.modules.features.TestLoggingFeature
import me.philippheuer.projectcfg.modules.policy.DefaultRepositoryPolicy
import me.philippheuer.projectcfg.modules.report.DependencyReport
import me.philippheuer.projectcfg.modules.type.JavaApplicationType
import me.philippheuer.projectcfg.modules.type.JavaLibraryType
import me.philippheuer.projectcfg.util.ModuleUtils
import me.philippheuer.projectcfg.util.PluginLogger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.slf4j.LoggerFactory

const val EXTENSION_NAME = "projectConfiguration"

abstract class ProjectConfigurationPlugin : Plugin<Project> {
    companion object {
        private val log = LoggerFactory.getLogger(ProjectConfigurationPlugin::class.java)

        fun allModules(ctx: ProjectContext): MutableList<PluginModule> {
            return mutableListOf(
                // policy
                DefaultRepositoryPolicy(ctx),
                // type
                JavaApplicationType(ctx),
                JavaLibraryType(ctx),
                // documentation
                DokkaDocumentation(ctx),
                JavadocDocumentation(ctx),
                // features
                JacocoFeature(ctx),
                PublishFeature(ctx),
                SigningFeature(ctx),
                LombokFeature(ctx),
                TestLoggingFeature(ctx),
                ShadowFeature(ctx),
                JUnit5Feature(ctx),
                LoggingLibraryFeature(ctx),
                GitPropertiesFeature(ctx),
                AutomaticModuleNameFeature(ctx),
                ReproducibleArchivesFeature(ctx),
                // check
                CheckstyleFeature(ctx),
                DetektFeature(ctx),
                // reporting
                DependencyReport(ctx),
            )
        }
    }

    override fun apply(project: Project) {
        val config = project.extensions.create(EXTENSION_NAME, ProjectConfigurationExtension::class.java)
        val ctx = ProjectContext(project, config)

        // logger
        PluginLogger.project = project
        PluginLogger.setLogLevel(config.logLevel.getOrElse(LogLevel.INFO))

        // module list
        val modules = allModules(ctx)

        // init module
        ModuleUtils.initModules(modules, project)

        // process features
        ModuleUtils.processModules(modules, project, config)
    }
}
