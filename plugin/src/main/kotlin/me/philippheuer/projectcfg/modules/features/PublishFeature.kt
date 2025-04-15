package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyPlugin
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import java.net.URI
import java.net.URISyntaxException

class PublishFeature(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        if (!ctx.isProjectType(ProjectType.LIBRARY)) {
            return false
        }

        var hasPublicationTarget = ctx.config.artifactRepository.isPresent
        if (ctx.project.properties.containsKey("repository.publish.target") && (ctx.project.properties["repository.publish.target"] as String).isNotEmpty()) {
            hasPublicationTarget = true
        } else if (System.getenv("MAVEN_REPO_URL") != null) {
            hasPublicationTarget = true
        }

        return hasPublicationTarget
    }

    override fun run() {
        configurePublish(ctx.project, ctx.config)
    }

    companion object {
        fun configurePublish(project: Project, config: ProjectConfigurationExtension) {
            // plugin
            project.applyPlugin("maven-publish")

            // configure
            project.extensions.configure(PublishingExtension::class.java) { publish ->
                if (config.artifactRepository.isPresent) {
                    publish.repositories.add(config.artifactRepository.get())
                } else {
                    val target = project.properties.getOrDefault("repository.publish.target", "") as String
                    if (project.properties.containsKey("repository.publish.$target.url")) {
                        PluginLogger.log(LogLevel.INFO, "configuring repository for publication for $target")
                        publish.repositories.add(
                            project.repositories.maven { m ->
                                m.name = target
                                m.url = URI(project.properties["repository.publish.$target.url"] as String)
                                m.credentials.run {
                                    username = project.properties["repository.publish.$target.username"] as String
                                    password = project.properties["repository.publish.$target.password"] as String
                                }
                            }
                        )
                    }
                }

                // environment based configuration
                val envRepoUrl = System.getenv("MAVEN_REPO_URL")?.trim()
                if (!envRepoUrl.isNullOrBlank()) {
                    val repoName = System.getenv("MAVEN_REPO_NAME")?.trim() ?: "maven"
                    val repoUser = System.getenv("MAVEN_REPO_USERNAME")?.trim()
                    val repoPassword = System.getenv("MAVEN_REPO_PASSWORD")?.trim()
                    PluginLogger.log(LogLevel.INFO, "configuring repository [$repoName] for publication from environment")

                    try {
                        publish.repositories.maven { m ->
                            m.name = repoName
                            m.url = URI(envRepoUrl)
                            if (!repoUser.isNullOrBlank() && !repoPassword.isNullOrBlank()) {
                                m.credentials.run {
                                    username = repoUser
                                    password = repoPassword
                                }
                            }
                        }
                    } catch (e: URISyntaxException) {
                        throw GradleException("Invalid MAVEN_REPO_URL: $envRepoUrl", e)
                    }
                }

                // publication
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