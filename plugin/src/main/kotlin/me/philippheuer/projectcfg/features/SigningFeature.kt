package me.philippheuer.projectcfg.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyProject
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.publish.PublishingExtension
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension

class SigningFeature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return config.type.get() == ProjectType.LIBRARY
    }

    override fun run() {
        // plugin
        project.applyProject("signing")

        // configure
        project.extensions.run {
            configure(PublishingExtension::class.java) { publish ->
                var publication = publish.publications.findByName("main")

                if (publication != null) {
                    PluginLogger.log(LogLevel.INFO, "configured signing for main publication")
                    configure(SigningExtension::class.java) {
                        it.useGpgCmd()
                        it.sign(publication)
                    }
                } else {
                    PluginLogger.log(LogLevel.WARN, "can't configure signing, no main publication found")
                }
            }
        }

        // toggle signing based on task graph
        project.tasks.withType(Sign::class.java) {
            it.onlyIf { !project.gradle.taskGraph.hasTask("publishToMavenLocal") }
        }
    }
}