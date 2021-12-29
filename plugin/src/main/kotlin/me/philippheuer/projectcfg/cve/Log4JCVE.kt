package me.philippheuer.projectcfg.cve

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.ProjectConfigurationPlugin
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyProject
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.api.internal.artifacts.dependencies.DefaultDependencyConstraint
import org.gradle.api.internal.artifacts.dependencies.DefaultImmutableVersionConstraint
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import java.net.URI

/**
 * dependency constraints for vulnerable Log4J versions
 *
 * The constraints will be applied to all project configurations regardless of usage.
 */
class Log4JCVE constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    companion object {
        fun applyConstraint(project: Project, config: ProjectConfigurationExtension) {
            project.configurations.forEach { configuration ->
                project.dependencies.constraints.add(configuration.name, "org.apache.logging.log4j:log4j-core") { constraint ->
                    constraint.version { v ->
                        v.strictly("[2.17, 3[")
                        v.prefer("2.17.0")
                    }
                    constraint.because("CVE-2021-44228, CVE-2021-45046, CVE-2021-45105: Log4j vulnerable to remote code execution and other critical security vulnerabilities")
                }
            }
        }
    }

    override fun check(): Boolean {
        return true
    }

    override fun run() {
        applyConstraint(project, config)
    }
}