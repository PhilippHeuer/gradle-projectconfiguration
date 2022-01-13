package me.philippheuer.projectcfg.features

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.util.PluginLogger
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

class VersionUpgradeFeature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        PluginLogger.log(LogLevel.INFO, "applying plugin [com.github.ben-manes.versions]")
        project.pluginManager.apply("com.github.ben-manes.versions")

        project.tasks.withType(DependencyUpdatesTask::class.java).configureEach { task ->
            // reject all non stable versions
            task.rejectVersionIf {
                isNonStable(it.candidate.version)
            }
        }
    }

    companion object {
        private fun isNonStable(version: String): Boolean {
            val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
            val regex = "^[0-9,.v-]+(-r)?$".toRegex()
            val isStable = stableKeyword || regex.matches(version)
            return isStable.not()
        }
    }
}