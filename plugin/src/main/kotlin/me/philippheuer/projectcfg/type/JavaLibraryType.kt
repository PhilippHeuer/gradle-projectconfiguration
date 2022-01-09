package me.philippheuer.projectcfg.type

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.addDepdenency
import me.philippheuer.projectcfg.util.applyProject
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class JavaLibraryType constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return config.type.get() == ProjectType.LIBRARY
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
        project.applyProject("java-library")
        project.applyProject("maven-publish")

        project.run {
            group = config.artifactGroupId.get()
            version = config.artifactVersion.get()

            extensions.run {
                configure(BasePluginExtension::class.java) {
                    it.archivesName.set(config.artifactId.get())
                }

                configure(JavaPluginExtension::class.java) {
                    // java version
                    it.sourceCompatibility = config.javaVersion.get()

                    // sources / javadocs
                    it.withSourcesJar()
                    it.withJavadocJar()

                    // sourceSets
                    it.sourceSets.getByName("main") { ss ->
                        ss.java.setSrcDirs(listOf("src/main/java", "src/main/kotlin"))
                    }
                    it.sourceSets.getByName("test") { ss ->
                        ss.java.setSrcDirs(listOf("src/test/java", "src/test/kotlin"))
                    }
                }
            }

            tasks.withType(JavaCompile::class.java).configureEach {
                it.options.encoding = config.fileEncoding.get()
                it.options.isIncremental = true
            }
        }
    }

    fun configureKotlinLibrary(project: Project, config: ProjectConfigurationExtension) {
        project.applyProject("org.jetbrains.kotlin.jvm")

        project.run {
            addDepdenency("api", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${DependencyVersion.kotlinVersion}")

            tasks.withType(KotlinCompile::class.java).configureEach {
                it.kotlinOptions.jvmTarget = config.javaVersionAsJvmVersion()
                it.kotlinOptions.javaParameters = true
                it.incremental = true
            }
        }
    }
}