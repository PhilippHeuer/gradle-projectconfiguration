package me.philippheuer.projectcfg.modules.features

import com.gorylenko.GitPropertiesPluginExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.applyPlugin

/**
 * Embed Git Properties for App-Builds
 *
 * @link https://github.com/n0mer/gradle-git-properties
 */
class GitPropertiesFeature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.isProjectType(ProjectType.APP)
    }

    override fun run() {
        configurePlugin(ctx)
        configureExtension(ctx)
    }

    companion object {
        /**
         * apply the plugin to the project
         */
        fun configurePlugin(ctx: IProjectContext) {
            ctx.project.applyPlugin("com.gorylenko.gradle-git-properties")
        }

        /**
         * configure the plugin
         */
        fun configureExtension(ctx: IProjectContext) {
            ctx.project.extensions.configure(GitPropertiesPluginExtension::class.java) {
                it.failOnNoGitDirectory = false
                it.gitPropertiesName = "git.properties"
                it.dateFormat = "yyyy-MM-dd'T'HH:mmZ"
                it.dateFormatTimeZone = "UTC"
                it.keys = listOf(
                    "git.branch",
                    "git.build.user.email",
                    "git.build.user.name",
                    "git.build.version",
                    "git.closest.tag.commit.count",
                    "git.closest.tag.name",
                    "git.commit.id",
                    "git.commit.id.abbrev",
                    "git.commit.id.describe",
                    "git.commit.message.full",
                    "git.commit.message.short",
                    "git.commit.time",
                    "git.commit.user.email",
                    "git.commit.user.name",
                    "git.dirty",
                    "git.remote.origin.url",
                    "git.tags",
                    "git.total.commit.count"
                )
            }
        }
    }
}