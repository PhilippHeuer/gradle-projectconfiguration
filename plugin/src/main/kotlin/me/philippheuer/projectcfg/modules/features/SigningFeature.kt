package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyPlugin
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.logging.LogLevel
import org.gradle.api.publish.PublishingExtension
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import java.util.Base64

class SigningFeature(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.project.isRootProjectWithoutSubprojectsOrSubproject() && ctx.isProjectType(ProjectType.LIBRARY)
    }

    override fun run() {
        // plugin
        ctx.project.applyPlugin("signing")

        // configure
        ctx.project.extensions.run {
            configure(PublishingExtension::class.java) { publish ->
                val signingKey = ctx.project.findProperty("signingKey") as String?
                val signingPassword = ctx.project.findProperty("signingPassword") as String?

                if (publish.publications.isEmpty()) {
                    PluginLogger.log(LogLevel.WARN, "no publications found, skipping signing configuration")
                    return@configure
                }

                if (!ctx.project.hasProperty("signing.gnupg.keyName") && signingKey == null) {
                    PluginLogger.log(LogLevel.WARN, "skipping signing for publication, signing.gnupg.keyName and signingKey not set")
                    return@configure
                }

                configure(SigningExtension::class.java) { signing ->
                    publish.publications.forEach { publication ->
                        PluginLogger.log(LogLevel.INFO, "configuring signing for publication '${publication.name}'")

                        if (signingKey != null) {
                            PluginLogger.log(LogLevel.DEBUG, "using ASCII armored key for signing")
                            val decodedSigningKey = Base64.getDecoder().decode(signingKey).toString(Charsets.UTF_8)
                            signing.useInMemoryPgpKeys(decodedSigningKey, signingPassword)
                        } else {
                            PluginLogger.log(LogLevel.DEBUG, "using GPG command for signing")
                            signing.useGpgCmd()
                            signing.isRequired = false // only sign if key is available
                        }

                        signing.sign(publication)
                    }
                }
            }
        }

        // toggle signing based on task graph
        ctx.project.tasks.withType(Sign::class.java) {
            it.onlyIf { !ctx.project.gradle.taskGraph.hasTask("publishToMavenLocal") }
        }
    }
}
