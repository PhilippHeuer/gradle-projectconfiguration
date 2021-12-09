package me.philippheuer.projectcfg.policy

import me.philippheuer.projectcfg.EXTENSION_NAME
import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.wrapper.Wrapper
import java.net.URL

/**
 * Policy - this will ensure that a tested version of gradle is used with this plugin
 */
class GradleWrapperVersionPolicy constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    var allowedVersions = listOf("7.3", "7.3.1")

    override fun check(): Boolean {
        log(LogLevel.DEBUG, "module check [$EXTENSION_NAME.gradleVersionCheckBypass] is [${config.gradleVersionPolicyEnabled.get()}]")
        return config.gradleVersionPolicyEnabled.get()
    }

    override fun run() {
        val suggestedVersion = allowedVersions.last()

        // configure wrapper task
        log(LogLevel.WARN, "set [gradle.version] to [${suggestedVersion}]")
        project.tasks.withType(Wrapper::class.java).configureEach {
            it.gradleVersion = suggestedVersion
            it.distributionType = Wrapper.DistributionType.BIN

            try {
                val wrapperDistTypeText = if (it.distributionType == Wrapper.DistributionType.BIN) "bin" else "all"
                it.distributionSha256Sum = URL("https://services.gradle.org/distributions/gradle-${it.gradleVersion}-$wrapperDistTypeText.zip.sha256").openStream().bufferedReader().use { b -> b.readText() }
            } catch (e: Exception) {
                log(LogLevel.WARN, "failed to fetch [gradle.checksum] - ${e.message}")
            }
        }

        // validate gradle version
        if (!allowedVersions.contains(project.gradle.gradleVersion)) {
            log(LogLevel.WARN, "checking [gradle.version] is [${project.gradle.gradleVersion}] result [not valid - one of $allowedVersions]")
            project.logger.error("Gradle ${project.gradle.gradleVersion} is not supported!")
        } else {
            log(LogLevel.INFO, "checking [gradle.version] is [${project.gradle.gradleVersion}] result [valid]")
        }
    }
}
