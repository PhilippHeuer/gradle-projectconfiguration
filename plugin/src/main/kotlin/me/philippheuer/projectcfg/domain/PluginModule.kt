package me.philippheuer.projectcfg.domain

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
}