package me.philippheuer.projectcfg.type

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyVersion
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinTest
import java.net.URI
import java.net.URL

class JavaLibraryType constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectType.LIBRARY == config.type.get()
    }

    override fun run() {
        if (config.language.get() == ProjectLanguage.JAVA) {
            configureJavaLibrary(project, config)
        } else if (config.language.get() == ProjectLanguage.KOTLIN) {
            configureJavaLibrary(project, config)
            configureKotlinLibrary(project, config)
        }
    }

    fun configureJavaLibrary(project: Project, config: ProjectConfigurationExtension) {
        project.run {
            log(LogLevel.INFO, "applying plugin [java-library]")
            pluginManager.apply("java-library")
            log(LogLevel.INFO, "applying plugin [maven-publish]")
            pluginManager.apply("maven-publish")

            group = config.artifactGroupId.get()
            version = config.artifactVersion.get()

            extensions.run {
                configure(JavaPluginExtension::class.java) {
                    // java version
                    it.sourceCompatibility = config.javaVersion.get()

                    // sources / javadocs
                    it.withSourcesJar()
                    it.withJavadocJar()
                }
            }

            tasks.withType(JavaCompile::class.java).configureEach {
                it.options.encoding = config.fileEncoding.get()
            }
        }
    }

    fun configureKotlinLibrary(project: Project, config: ProjectConfigurationExtension) {
        project.run {
            log(LogLevel.INFO, "applying plugin [org.jetbrains.kotlin.jvm]")
            pluginManager.apply("org.jetbrains.kotlin.jvm")

            dependencies.add("api", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${DependencyVersion.kotlinVersion}")

            tasks.withType(KotlinCompile::class.java).configureEach {
                it.kotlinOptions.jvmTarget = config.javaVersion.get().toString()
            }
        }
    }
}
