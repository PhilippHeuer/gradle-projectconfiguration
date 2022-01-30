package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyPlugin
import org.gradle.api.logging.LogLevel
import org.gradle.api.publish.PublishingExtension
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension

class SigningFeature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.isProjectType(ProjectType.LIBRARY)
    }

    override fun run() {
        // plugin
        ctx.project.applyPlugin("signing")

        // configure
        ctx.project.extensions.run {
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
        ctx.project.tasks.withType(Sign::class.java) {
            it.onlyIf { !ctx.project.gradle.taskGraph.hasTask("publishToMavenLocal") }
        }
    }
}