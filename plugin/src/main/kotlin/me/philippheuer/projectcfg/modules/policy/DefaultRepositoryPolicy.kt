package me.philippheuer.projectcfg.modules.policy

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.util.PluginLogger
import org.gradle.api.logging.LogLevel

class DefaultRepositoryPolicy constructor(override var ctx: IProjectContext) : PluginModule {

    override fun init() {
        super.init()

        if (ctx.project.repositories.size == 0) {
            PluginLogger.log(LogLevel.INFO, "no repository configured, adding mavenCentral")
            ctx.project.repositories.add(ctx.project.repositories.mavenCentral())
        }
    }
}
