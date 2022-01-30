package me.philippheuer.projectcfg.modules.library

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectLibraries
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.addDependency
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject

class SentryLibrary constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.project.isRootProjectWithoutSubprojectsOrSubproject() && ctx.isProjectTypeIn(listOf(ProjectType.APP, ProjectType.BATCH)) && ctx.isProjectLibrary(ProjectLibraries.SENTRYIO)
    }

    override fun run() {
        applyDependencies(ctx)
    }

    companion object {
        fun applyDependencies(ctx: IProjectContext) {
            if (ctx.isProjectFramework(ProjectFramework.SPRINGBOOT)) {
                // https://docs.sentry.io/platforms/java/guides/spring-boot/
                ctx.project.addDependency("io.sentry:sentry-spring-boot-starter:${DependencyVersion.sentryVersion}")
            } else if (ctx.isProjectFramework(ProjectFramework.QUARKUS)) {
                // https://quarkiverse.github.io/quarkiverse-docs/quarkus-logging-sentry/dev/index.html
                ctx.project.addDependency("io.quarkiverse.loggingsentry:quarkus-logging-sentry:1.0.2")
            }
        }
    }
}