package me.philippheuer.projectcfg.util

import me.philippheuer.projectcfg.config.PluginConfig
import me.philippheuer.projectcfg.domain.PluginModule
import org.gradle.api.Project

class ModuleUtils {
    companion object {
        /**
         * Initializes the modules
         */
        fun initModules(modules: List<PluginModule>, project: Project) {
            modules.forEach {
                PluginLogger.setContext(project, "${it::class.java}")
                it.init()
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
                modules.forEach {
                    PluginLogger.setContext(project, "${it::class.java}")
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
}
