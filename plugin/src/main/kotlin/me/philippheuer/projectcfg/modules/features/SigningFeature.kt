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
import java.util.Base64

class SigningFeature(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.isProjectType(ProjectType.LIBRARY)
    }

    override fun run() {
        // plugin
        ctx.project.applyPlugin("signing")

        // configure
        ctx.project.extensions.run {
            configure(PublishingExtension::class.java) { publish ->
                val signingKey = ctx.project.findProperty("signingKey") as String?
                val signingPassword = ctx.project.findProperty("signingPassword") as String?

                val publication = publish.publications.findByName("main")
                if (publication == null) {
                    PluginLogger.log(LogLevel.WARN, "can't configure signing, no main publication found")
                    return@configure
                }

                if (!ctx.project.hasProperty("signing.gnupg.keyName") && signingKey == null) {
                    PluginLogger.log(LogLevel.WARN, "skipping signing for main publication, signing.gnupg.keyName and signingKey not set")
                    return@configure
                }

                PluginLogger.log(LogLevel.INFO, "configuring signing for main publication")
                configure(SigningExtension::class.java) {
                    if (signingKey != null) {
                        PluginLogger.log(LogLevel.INFO, "using ASCII armored key for signing")
                        val decodedSigningKey =  Base64.getDecoder().decode(signingKey).toString(Charsets.UTF_8)
                        it.useInMemoryPgpKeys(decodedSigningKey, signingPassword)
                    } else {
                        PluginLogger.log(LogLevel.INFO, "using GPG command for signing")
                        it.useGpgCmd()
                        it.isRequired = false // only sign if key is available
                    }

                    it.sign(publication)
                }
            }
        }

        // toggle signing based on task graph
        ctx.project.tasks.withType(Sign::class.java) {
            it.onlyIf { !ctx.project.gradle.taskGraph.hasTask("publishToMavenLocal") }
        }
    }
}