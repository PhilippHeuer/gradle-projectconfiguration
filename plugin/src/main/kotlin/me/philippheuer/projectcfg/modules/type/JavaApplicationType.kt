package me.philippheuer.projectcfg.modules.type

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.addDependency
import me.philippheuer.projectcfg.util.applyPlugin
import me.philippheuer.projectcfg.util.toJavaLanguageVersion
import me.philippheuer.projectcfg.util.toJvmTarget
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

class JavaApplicationType constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.isProjectType(ProjectType.APP) || ctx.isProjectType(ProjectType.BATCH)
    }

    override fun run() {
        configureJavaApplication(ctx.project, ctx.config)
        if (ctx.config.language.get() == ProjectLanguage.KOTLIN) {
            configureKotlinApplication(ctx.project, ctx.config)
        }
    }

    companion object {
        fun configureJavaApplication(project: Project, config: ProjectConfigurationExtension) {
            project.applyPlugin("java")

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
                        PluginLogger.log(LogLevel.INFO, "set sourceCompatibility = ${it.sourceCompatibility}, targetCompatibility = ${it.targetCompatibility}")

                        // toolchain
                        if (config.javaToolchainVersion.isPresent) {
                            it.toolchain.languageVersion.set(config.javaToolchainVersion.map { jv -> jv.toJavaLanguageVersion() }.get())
                            PluginLogger.log(LogLevel.INFO, "set toolchain.languageVersion = ${it.toolchain.languageVersion.get()}")
                        }

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
            project.applyPlugin("application")
            project.applyPlugin("org.jetbrains.kotlin.jvm")

            project.run {
                addDependency("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${DependencyVersion.kotlinVersion}")
                addDependency("testImplementation", "org.jetbrains.kotlin:kotlin-test:${DependencyVersion.kotlinVersion}")

                tasks.withType(KotlinJvmCompile::class.java).configureEach {
                    it.compilerOptions { co ->
                        co.jvmTarget.set(config.javaVersion.map { jv -> jv.toJvmTarget() }.get())
                        co.javaParameters.set(true)
                    }
                }

                // Workaround for https://youtrack.jetbrains.com/issue/KT-54207
                tasks.getByName("kotlinSourcesJar").enabled = false
            }
        }
    }
}
