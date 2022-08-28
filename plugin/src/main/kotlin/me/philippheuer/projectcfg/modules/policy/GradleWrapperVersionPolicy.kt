package me.philippheuer.projectcfg.modules.policy

import me.philippheuer.projectcfg.EXTENSION_NAME
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.util.PluginLogger
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.wrapper.Wrapper
import java.net.URL

/**
 * Policy - this will ensure that a tested version of gradle is used with this plugin
 */
class GradleWrapperVersionPolicy constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        PluginLogger.log(LogLevel.DEBUG, "module check [$EXTENSION_NAME.gradleVersionCheckBypass] is [${ctx.config.gradleVersionPolicyEnabled.get()}]")
        return ctx.config.gradleVersionPolicyEnabled.get()
    }

    override fun run() {
        // only run for root project
        if (ctx.project.rootProject == ctx.project) {
            checkGradleVersion(ctx.project)
        }
    }

    companion object {
        private const val suggestedVersion = "7.5.1"

        fun checkGradleVersion(project: Project) {
            // configure wrapper task
            PluginLogger.log(LogLevel.WARN, "set [gradle.version] to [$suggestedVersion]")
            project.tasks.withType(Wrapper::class.java).configureEach {
                it.gradleVersion = suggestedVersion
                it.distributionType = Wrapper.DistributionType.BIN

                try {
                    val wrapperDistTypeText = if (it.distributionType == Wrapper.DistributionType.BIN) "bin" else "all"
                    it.distributionSha256Sum = URL("https://services.gradle.org/distributions/gradle-${it.gradleVersion}-$wrapperDistTypeText.zip.sha256").openStream().bufferedReader().use { b -> b.readText() }
                } catch (e: Exception) {
                    PluginLogger.log(LogLevel.WARN, "failed to fetch [gradle.checksum] - ${e.message}")
                }
            }
        }
    }
}