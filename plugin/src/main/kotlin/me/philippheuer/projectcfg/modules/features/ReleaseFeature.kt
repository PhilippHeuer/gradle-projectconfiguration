package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.api.logging.LogLevel
import org.gradle.api.publish.PublishingExtension
import org.gradle.authentication.http.HttpHeaderAuthentication
import java.net.URI

class ReleaseFeature(override var ctx: IProjectContext) : PluginModule {
    data class ResolvedReleaseRepository(
        val name: String,
        val url: URI,
        val username: String? = null,
        val password: String? = null,
        val headerName: String? = null,
        val headerValue: String? = null,
        val type: String = "nexus2"
    )

    override fun check(): Boolean {
        return ctx.project.isRootProjectWithoutSubprojectsOrSubproject() && ctx.isProjectType(ProjectType.LIBRARY)
    }

    override fun run() {
        configurePublish(ctx.project, ctx.config)
    }

    companion object {
        fun configurePublish(project: Project, config: ProjectConfigurationExtension) {
            val repoConfig = discoverRepository(project, config)
            if (repoConfig == null) {
                PluginLogger.log(LogLevel.INFO, "No repository configuration found, skipping publish repository configuration")
                return
            }

            project.extensions.configure(PublishingExtension::class.java) { publish ->
                PluginLogger.log(LogLevel.INFO, "Configuring publish repository: ${repoConfig.name} (${repoConfig.url})")
                publish.repositories.maven { m ->
                    m.name = repoConfig.name
                    m.url = repoConfig.url

                    if (repoConfig.headerName != null && repoConfig.headerValue != null) {
                        PluginLogger.log(LogLevel.INFO, "Using HTTP header authentication: ${repoConfig.headerName}")
                        m.credentials(HttpHeaderCredentials::class.java) { c ->
                            c.name = repoConfig.headerName
                            c.value = repoConfig.headerValue
                        }
                        m.authentication { auth ->
                            auth.create("header", HttpHeaderAuthentication::class.java)
                        }
                    } else {
                        m.credentials.run {
                            username = repoConfig.username
                            password = repoConfig.password
                        }
                    }
                }
            }
        }

        fun discoverRepository(project: Project, config: ProjectConfigurationExtension): ResolvedReleaseRepository? {
            // prio 1: environment based configuration
            val envRepoUrl = System.getenv("MAVEN_REPO_URL")?.trim()
            if (!envRepoUrl.isNullOrBlank()) {
                val repoName = System.getenv("MAVEN_REPO_NAME")?.trim() ?: "maven"
                val repoUser = System.getenv("MAVEN_REPO_USERNAME")?.trim()
                val repoPassword = System.getenv("MAVEN_REPO_PASSWORD")?.trim()
                val repoType = System.getenv("MAVEN_REPO_TYPE")?.trim() ?: "nexus2"
                val (headerName, headerValue) = resolveHeader(
                    System.getenv("MAVEN_REPO_HEADER_NAME"),
                    System.getenv("MAVEN_REPO_HEADER_VALUE"),
                    "MAVEN_REPO_HEADER_* environment variables"
                )

                return ResolvedReleaseRepository(
                    name = repoName,
                    url = URI(envRepoUrl),
                    username = repoUser,
                    password = repoPassword,
                    headerName = headerName,
                    headerValue = headerValue,
                    type = repoType
                )
            }

            // prio 2: project property configuration
            val target = project.findProperty("repository.publish.target")?.toString()?.trim().orEmpty()
            val baseKey = "repository.publish.$target"
            val url = project.findProperty("${baseKey}.url")?.toString()?.trim()
            if (!url.isNullOrBlank()) {
                val name = project.findProperty("${baseKey}.name")?.toString()?.trim() ?: "maven"
                val username = project.findProperty("${baseKey}.username")?.toString()?.trim()
                val password = project.findProperty("${baseKey}.password")?.toString()?.trim()
                val type = project.findProperty("${baseKey}.type")?.toString()?.trim() ?: "nexus2"
                val (headerName, headerValue) = resolveHeader(
                    project.findProperty("${baseKey}.headerName")?.toString(),
                    project.findProperty("${baseKey}.headerValue")?.toString(),
                    "properties ${baseKey}.headerName / ${baseKey}.headerValue"
                )

                return ResolvedReleaseRepository(
                    name = name,
                    url = URI(url),
                    username = username,
                    password = password,
                    headerName = headerName,
                    headerValue = headerValue,
                    type = type
                )
            }

            // prio 3: manual configuration
            if (config.artifactRepository.isPresent) {
                val repo = config.artifactRepository.get()
                if (repo !is MavenArtifactRepository) {
                    throw GradleException("Artifact repository must be of type MavenArtifactRepository")
                }

                return ResolvedReleaseRepository(
                    name = repo.name,
                    url = repo.url,
                    username = repo.credentials.username,
                    password = repo.credentials.password,
                    type = "nexus2"
                )
            }

            return null
        }

        private fun resolveHeader(name: String?, value: String?, source: String): Pair<String?, String?> {
            val headerName = name?.trim()?.takeIf { it.isNotBlank() }
            val headerValue = value?.trim()?.takeIf { it.isNotBlank() }

            return when {
                headerName != null && headerValue != null -> headerName to headerValue
                headerName == null && headerValue == null -> null to null
                else -> {
                    PluginLogger.log(LogLevel.WARN, "Incomplete HTTP header credentials in $source, ignoring (both header name and value are required)")
                    null to null
                }
            }
        }
    }
}
