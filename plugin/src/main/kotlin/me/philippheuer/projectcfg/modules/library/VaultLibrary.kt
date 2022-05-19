package me.philippheuer.projectcfg.modules.library

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectLibraries
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.addDependency
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject

class VaultLibrary constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.project.isRootProjectWithoutSubprojectsOrSubproject() && ctx.isProjectTypeIn(listOf(ProjectType.APP, ProjectType.BATCH)) && ctx.isProjectLibrary(ProjectLibraries.VAULT)
    }

    override fun run() {
        applyDependencies(ctx)
    }

    companion object {
        fun applyDependencies(ctx: IProjectContext) {
            if (ctx.isProjectFramework(ProjectFramework.SPRINGBOOT)) {
                // https://search.maven.org/artifact/org.springframework.cloud/spring-cloud-vault-config
                // https://www.baeldung.com/spring-cloud-vault
                ctx.project.addDependency("org.springframework.cloud:spring-cloud-starter-vault-config:3.1.0")
                ctx.project.addDependency("org.springframework.cloud:spring-cloud-vault-config-databases:3.1.0")

            } else if (ctx.isProjectFramework(ProjectFramework.QUARKUS)) {
                // https://quarkiverse.github.io/quarkiverse-docs/quarkus-vault/dev/index.html
                ctx.project.addDependency("io.quarkiverse.vault:quarkus-vault:1.0.1")
            }
        }
    }
}