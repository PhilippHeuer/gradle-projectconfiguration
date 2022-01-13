package me.philippheuer.projectcfg.cve

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import org.gradle.api.Project

/**
 * dependency constraints for vulnerable Log4J versions
 *
 * The constraints will be applied to all project configurations regardless of usage.
 */
class Log4JCVE constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        applyConstraint(ctx.project)
    }

    companion object {
        fun applyConstraint(project: Project) {
            project.configurations.forEach { configuration ->
                project.dependencies.constraints.add(configuration.name, "org.apache.logging.log4j:log4j-core") { constraint ->
                    constraint.version { v ->
                        v.strictly("[2.17, 3[")
                        v.prefer("2.17.1")
                    }
                    constraint.because("CVE-2021-44228, CVE-2021-45046, CVE-2021-45105: Log4j vulnerable to remote code execution and other critical security vulnerabilities")
                }
            }
        }
    }
}