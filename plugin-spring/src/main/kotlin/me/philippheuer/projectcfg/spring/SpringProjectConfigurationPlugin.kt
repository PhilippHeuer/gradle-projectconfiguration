package me.philippheuer.projectcfg.spring

import me.philippheuer.projectcfg.ProjectConfigurationPlugin
import me.philippheuer.projectcfg.domain.ProjectContext
import me.philippheuer.projectcfg.spring.springframework.SpringBootFramework
import me.philippheuer.projectcfg.util.ModuleUtils
import me.philippheuer.projectcfg.util.PluginLogger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

const val EXTENSION_NAME = "springProjectConfiguration"

abstract class SpringProjectConfigurationPlugin : Plugin<Project> {
    companion object {
        private val log = LoggerFactory.getLogger(SpringProjectConfigurationPlugin::class.java)
    }

    override fun apply(project: Project) {
        val config = project.extensions.create(EXTENSION_NAME, SpringProjectConfigurationExtension::class.java)
        val ctx = ProjectContext(project, config)

        // logger
        PluginLogger.project = project
        PluginLogger.setLogLevel(config.logLevel.getOrElse(null))

        // module list
        val modules = ProjectConfigurationPlugin.allModules(ctx)
        modules.add(SpringBootFramework(ctx))

        // init module
        ModuleUtils.initModules(modules, project)

        // process features
        ModuleUtils.processModules(modules, project, config)
    }
}
