package me.philippheuer.projectcfg.policy

import me.philippheuer.projectcfg.EXTENSION_NAME
import me.philippheuer.projectcfg.domain.PluginModule
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.wrapper.Wrapper

/**
 * Policy - this will ensure that a tested version of gradle is used with this plugin
 */
class GradleWrapperVersionPolicy constructor(override var project: Project, override var config: me.philippheuer.projectcfg.ProjectConfigurationExtension) : PluginModule {
    var allowedVersions = listOf("7.3")

    override fun check(): Boolean {
        log(LogLevel.DEBUG, "module check [$EXTENSION_NAME.gradleVersionCheckBypass] is [${config.gradleVersionPolicyEnabled.get()}]")
        return config.gradleVersionPolicyEnabled.get()
    }

    override fun run() {
        project.run {
            if (!allowedVersions.contains(gradle.gradleVersion)) {
                log(LogLevel.WARN, "checking [gradle.version] is [${gradle.gradleVersion}] result [not valid - one of $allowedVersions]")

                val suggestedVersion = allowedVersions.last()
                log(LogLevel.WARN, "set [gradle.version] to [${suggestedVersion}]")
                project.tasks.withType(Wrapper::class.java).configureEach {
                    it.gradleVersion = suggestedVersion
                    it.distributionType = Wrapper.DistributionType.BIN
                }
            } else {
                log(LogLevel.INFO, "checking [gradle.version] is [${gradle.gradleVersion}] result [valid]")
            }
        }
    }
}
