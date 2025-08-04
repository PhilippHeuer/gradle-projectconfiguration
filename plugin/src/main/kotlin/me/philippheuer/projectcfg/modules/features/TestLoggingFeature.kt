package me.philippheuer.projectcfg.modules.features

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.util.PluginHelper
import me.philippheuer.projectcfg.util.applyPlugin
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject

class TestLoggingFeature(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.project.isRootProjectWithoutSubprojectsOrSubproject()
    }

    override fun run() {
        // plugin
        ctx.project.applyPlugin("com.adarshr.test-logger")

        // configure
        ctx.project.extensions.configure(TestLoggerExtension::class.java) {
            if (PluginHelper.isCI() || PluginHelper.isIDEA()) {
                it.theme = ThemeType.PLAIN
            } else {
                it.theme = ThemeType.MOCHA_PARALLEL
            }
            it.slowThreshold = 1000
        }
    }
}
