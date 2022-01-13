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

class JavaApplicationType constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectType.APP.valueEquals(config.type.get()) || ProjectType.BATCH.valueEquals(config.type.get())
    }

    override fun run() {
        configureJavaApplication(project, config)
        if (config.language.get() == ProjectLanguage.KOTLIN) {
            configureKotlinApplication(project, config)
        }
    }

    companion object {
        fun configureJavaApplication(project: Project, config: ProjectConfigurationExtension) {
            project.applyProject("java")

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
                        it.targetCompatibility = config.javaVersion.get()

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
                }
            }
        }

        fun configureKotlinApplication(project: Project, config: ProjectConfigurationExtension) {
            project.applyProject("org.jetbrains.kotlin.jvm")

            project.run {
                addDepdenency("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${DependencyVersion.kotlinVersion}")

                tasks.withType(KotlinCompile::class.java).configureEach {
                    it.kotlinOptions.jvmTarget = config.javaVersionAsJvmVersion()
                    it.incremental = true
                }
            }
        }
    }
}
