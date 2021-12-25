package me.philippheuer.projectcfg.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import java.net.URI

class PublishFeature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return config.type.get() == ProjectType.LIBRARY
    }

    override fun run() {
        // plugin
        project.pluginManager.apply("maven-publish")

        // configure
        project.extensions.configure(PublishingExtension::class.java) { publish ->
            // only configure if a target repository has been configured
            if (config.artifactRepository.isPresent) {
                publish.repositories.add(config.artifactRepository.get())
            } else {
                publish.repositories.add(
                    project.repositories.maven { m ->
                        val target = project.properties["repository.publish.target"] as String
                        m.name = target
                        m.url = URI(project.properties["repository.publish.$target.url"] as String)
                        m.credentials.run {
                            username = project.properties["repository.publish.$target.username"] as String
                            password = project.properties["repository.publish.$target.password"] as String
                        }
                    }
                )
            }

            publish.publications.create("main", MavenPublication::class.java) { pub ->
                pub.from(project.components.getByName("java"))
                pub.groupId = config.artifactGroupId.get()
                pub.artifactId = config.artifactId.get()
                pub.version = config.artifactVersion.get()
                pub.pom { pom ->
                    // customize pom defaults if provided
                    config.pom.invoke(pom)
                    pom.name.set(project.displayName)
                    pom.description.set(project.description)
                }

                log(LogLevel.INFO, "configured artifact: ${pub.groupId}:${pub.artifactId}:${pub.version}")
            }
        }
    }
}