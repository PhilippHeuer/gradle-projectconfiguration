package me.philippheuer.projectcfg.modules.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.modules.framework.tasks.QuarkusConfigurationTask
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.PluginHelper
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.addDependency
import me.philippheuer.projectcfg.util.addPlatformDependency
import me.philippheuer.projectcfg.util.applyPlugin
import org.gradle.api.Project
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

private const val CONFIG_TASK_NAME = "projectcfg-resources-quarkus-properties"

class QuarkusFramework constructor(override var ctx: IProjectContext) : PluginModule {
    override fun init() {
        applyConstraint(ctx)
    }

    override fun check(): Boolean {
        return ctx.isProjectFramework(ProjectFramework.QUARKUS)
    }

    override fun run() {
        if (ctx.isProjectType(ProjectType.LIBRARY)) {
            configureLibrary(ctx)
            configDefaults(ctx)
        } else if (ctx.isProjectType(ProjectType.APP)) {
            applyPlugin(ctx.project, ctx.config)
            configDefaults(ctx)
        }
    }

    companion object {
        fun applyConstraint(ctx: IProjectContext) {
            ctx.project.addPlatformDependency("io.quarkus.platform:quarkus-bom:${DependencyVersion.quarkusVersion}")
        }

        fun configureLibrary(ctx: IProjectContext) {
            // core
            ctx.project.addDependency("io.quarkus:quarkus-core:${DependencyVersion.quarkusVersion}")
            // health
            ctx.project.addDependency("io.quarkus:quarkus-smallrye-health:${DependencyVersion.quarkusVersion}")
            // metrics
            ctx.project.addDependency("io.quarkus:quarkus-micrometer:${DependencyVersion.quarkusVersion}")
            // test
            ctx.project.addDependency("io.quarkus:quarkus-junit5:${DependencyVersion.quarkusVersion}")
        }

        fun applyPlugin(project: Project, config: ProjectConfigurationExtension) {
            project.run {
                // plugin
                applyPlugin("io.quarkus")

                // health
                addDependency("implementation", "io.quarkus:quarkus-smallrye-health:${DependencyVersion.quarkusVersion}")

                // cache
                addDependency("implementation", "io.quarkus:quarkus-cache:${DependencyVersion.quarkusVersion}")

                // logging
                addDependency("implementation", "org.jboss.slf4j:slf4j-jboss-logmanager:1.1.0.Final")
                addDependency("implementation", "io.quarkus:quarkus-logging-json:${DependencyVersion.quarkusVersion}")

                // kotlin
                if (config.language.get() == ProjectLanguage.KOTLIN) {
                    applyPlugin("org.jetbrains.kotlin.plugin.allopen")

                    addDependency("implementation", "io.quarkus:quarkus-kotlin:${DependencyVersion.quarkusVersion}")

                    extensions.configure(AllOpenExtension::class.java) {
                        it.annotation("io.quarkus.test.junit.QuarkusTest")
                        it.annotation("javax.enterprise.context.ApplicationScoped")
                    }
                }

                // rest
                if (config.frameworkWebApi.get()) {
                    addDependency("implementation", "io.quarkus:quarkus-resteasy:${DependencyVersion.quarkusVersion}")
                }

                // metrics
                if (config.frameworkMetrics.get()) {
                    addDependency("implementation", "io.quarkus:quarkus-micrometer-registry-prometheus:${DependencyVersion.quarkusVersion}")
                }

                // tracing
                if (config.frameworkTracing.get()) {
                    addDependency("implementation", "io.quarkus:quarkus-smallrye-opentracing:${DependencyVersion.quarkusVersion}")
                }

                // db
                if (config.frameworkDb.get()) {
                    if (config.language.get() == ProjectLanguage.JAVA) {
                        addDependency("implementation", "io.quarkus:quarkus-hibernate-orm-panache:${DependencyVersion.quarkusVersion}")
                    } else if (config.language.get() == ProjectLanguage.KOTLIN) {
                        addDependency("implementation", "io.quarkus:quarkus-hibernate-orm-panache-kotlin:${DependencyVersion.quarkusVersion}")
                    }
                }

                // db migrations
                if (config.frameworkDbMigrate.get()) {
                    addDependency("implementation", "io.quarkus:quarkus-flyway:${DependencyVersion.quarkusVersion}")
                }

                // test
                addDependency("testImplementation", "io.quarkus:quarkus-junit5:${DependencyVersion.quarkusVersion}")

                // hibernate jandex constraint
                if (DependencyUtils.hasOneOfDependency(PluginLogger.project, listOf("implementation"), listOf("io.quarkus:quarkus-hibernate-orm", "io.quarkus:quarkus-hibernate-orm-panache", "io.quarkus:quarkus-hibernate-orm-panache-kotlin"))) {
                    project.dependencies.constraints.add("implementation", "org.jboss:jandex") { constraint ->
                        constraint.version { v ->
                            v.strictly("[2.4, 3[")
                            v.prefer("2.4.1.Final")
                        }
                        constraint.because("quarkus > 2.2 is not compatible with jandex < 2.4 (jandex index format version 10)")
                    }
                }
            }
        }

        fun configDefaults(ctx: IProjectContext) {
            configBuildTime(ctx)
            configRuntime(ctx)
        }

        private fun configBuildTime(ctx: IProjectContext) {
            val properties = mutableMapOf<String, String>()
            if (ctx.config.native.get()) {
                properties["quarkus.package.type"] = "native"
            } else {
                properties["quarkus.package.type"] = "fast-jar"
            }
            properties["quarkus.native.container-build"] = "true"
            properties["quarkus.native.builder-image"] = "quay.io/quarkus/ubi-quarkus-native-image:21.3.0-java17"
            properties["quarkus.ssl.native"] = "true"

            // TODO: copy this into a different resources directory and add it to resource paths
            PluginHelper.createOrUpdatePropertyFile(ctx.project, ctx.project.file("src/main/resources/META-INF/microprofile-config.properties"), properties, managed = true)
        }

        private fun configRuntime(ctx: IProjectContext) {
            // properties edit task
            val task = ctx.project.tasks.register(CONFIG_TASK_NAME, QuarkusConfigurationTask::class.java) {
                it.config = ctx.config
            }
            ctx.project.tasks.matching { it.name == "classes" }.configureEach {
                it.dependsOn(task)
                it.mustRunAfter("processResources")
            }
        }

    }
}