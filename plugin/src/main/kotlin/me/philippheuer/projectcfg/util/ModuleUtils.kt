package me.philippheuer.projectcfg.util

import me.philippheuer.projectcfg.config.PluginConfig
import me.philippheuer.projectcfg.domain.PluginModule
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import java.util.Collections

class ModuleUtils {
    companion object {
        /**
         * Initializes the modules
         */
        fun initModules(modules: List<PluginModule>, project: Project) {
            modules.forEach { module ->
                val moduleName = module.name()
                PluginLogger.setContext(project, moduleName)
                module.init()
            }
        }

        /**
         * Processes the modules
         * <p>
         * This will run the module checks and apply the module if the conditions are met.
         */
        fun processModules(modules: List<PluginModule>, project: Project, config: PluginConfig) {
            project.afterEvaluate { // TODO: config property values are only accessible in afterEvaluate, but there should be a better way maybe?
                // post process config
                config.postProcess()

                // process module
                modules.forEach { module ->
                    val moduleName = module.name()

                    PluginLogger.setContext(project, moduleName)
                    val enabled = module.check()
                    val isDisabled = config.disablePluginModules.getOrElse(Collections.emptyList()).contains(moduleName)
                    PluginLogger.log(LogLevel.DEBUG, "module [$moduleName] is enabled:[${enabled}], onDisabledPluginsList:[${isDisabled}] - ${config.disablePluginModules.get().toString()}")
                    if (enabled && !isDisabled) {
                        module.run()
                    }
                    PluginLogger.setContext(project, null)
                }
            }
        }
    }
}
