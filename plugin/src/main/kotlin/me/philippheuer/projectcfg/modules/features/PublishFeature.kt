package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyPlugin
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

class PublishFeature(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.project.isRootProjectWithoutSubprojectsOrSubproject() && ctx.isProjectType(ProjectType.LIBRARY)
    }

    override fun run() {
        configurePublish(ctx.project, ctx.config)
    }

    companion object {
        fun configurePublish(project: Project, config: ProjectConfigurationExtension) {
            // plugin
            project.applyPlugin("maven-publish")

            // create publication
            project.extensions.configure(PublishingExtension::class.java) { publish ->
                if (publish.publications.isEmpty()) {
                    publish.publications.create("main", MavenPublication::class.java) { pub ->
                        if (project.pluginManager.hasPlugin("java-platform")) {
                            pub.from(project.components.getByName("javaPlatform")) // BOM
                        } else if (project.pluginManager.hasPlugin("version-catalog")) {
                            pub.from(project.components.getByName("versionCatalog")) // Gradle Version Catalog
                        } else {
                            pub.from(project.components.getByName("java"))
                        }

                        pub.groupId = config.artifactGroupId.get()
                        pub.artifactId = config.artifactId.get()
                        pub.version = config.artifactVersion.get()
                        pub.pom { pom ->
                            pom.name.set(config.artifactDisplayName.getOrElse(project.displayName))
                            pom.description.set(config.artifactDescription.getOrElse(""))

                            if (project.pluginManager.hasPlugin("java-platform")) {
                                pom.packaging = "pom"
                            }

                            // customize pom
                            config.pom.invoke(pom)
                        }

                        PluginLogger.log(LogLevel.INFO, "configured artifact: ${pub.groupId}:${pub.artifactId}:${pub.version}")
                    }
                }
            }
        }
    }
}