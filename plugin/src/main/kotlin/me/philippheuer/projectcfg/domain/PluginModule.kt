package me.philippheuer.projectcfg.domain

import me.philippheuer.projectcfg.util.DependencyUtils

interface PluginModule {
    var ctx: IProjectContext

    /**
     * default init code, but always run before afterEvaluate when config is not available yet
     */
    fun init() {

    }

    /**
     * checks if this module should be enabled
     * @return boolean
     */
    fun check(): Boolean {
        return false
    }

    /**
     * configures this module / feature, only call after check
     */
    fun run() {

    }

    fun isProjectLanguage(language: IProjectLanguage): Boolean {
        return language.valueEquals(ctx.config.language.get())
    }

    fun isProjectType(type: IProjectType): Boolean {
        return type.valueEquals(ctx.config.type.get())
    }

    fun isProjectFramework(framework: IProjectFramework): Boolean {
        return framework.valueEquals(ctx.config.framework.get())
    }

    fun hasProjectDependency(dependencyNotation: String): Boolean {
        return DependencyUtils.hasDependency(ctx.project, listOf("implementation", "api"), dependencyNotation)
    }
}