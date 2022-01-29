package me.philippheuer.projectcfg.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.PluginHelper
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.addDependency
import me.philippheuer.projectcfg.util.addPlatformDependency
import me.philippheuer.projectcfg.util.applyPlugin
import org.gradle.api.Project
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

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
        } else if (ctx.isProjectType(ProjectType.APP)) {
            applyPlugin(ctx.project, ctx.config)
            quarkusDefaults(ctx.project, ctx.config)
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

            // add empty beans.xml to make quarkus scan classes in each library module
            val beansFile = ctx.project.file("src/main/resources/META-INF/beans.xml")
            if (!beansFile.exists()) {
                beansFile.createNewFile()
            }
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

        fun quarkusDefaults(project: Project, config: ProjectConfigurationExtension) {
            // see: https://quarkus.io/guides/all-config
            val properties = mutableMapOf(
                // banner
                "quarkus.banner.enabled" to "false",

                // tune reload performance with instrumentation
                "quarkus.live-reload.instrumentation" to "true",

                // http
                "quarkus.http.host" to "0.0.0.0",
                "quarkus.http.port" to "8080",

                // graceful exit
                "quarkus.shutdown.timeout" to "15",

                // logging
                "quarkus.log.level" to "INFO",
                "quarkus.log.min-level" to "DEBUG",
                "quarkus.log.console.json" to "false",
                "quarkus.log.console.async" to "true",
                "quarkus.log.console.async.queue-length" to "512",

                // metrics
                "quarkus.log.metrics.enabled" to "true",

                // ui
                "quarkus.application.ui-header" to "${config.artifactId.get()} v${config.artifactVersion.get()}",
                "quarkus.health.openapi.included" to "false",
                "quarkus.smallrye-health.ui.always-include" to "true",
            )

            // metrics
            if (DependencyUtils.hasDependency(project, listOf("implementation"), "io.quarkus:quarkus-agroal")) {
                properties["quarkus.datasource.metrics.enabled"] = "true"
            }
            if (DependencyUtils.hasDependency(project, listOf("implementation"), "io.quarkus:quarkus-scheduler")) {
                properties["quarkus.scheduler.metrics.enabled"] = "true"
            }
            if (DependencyUtils.hasDependency(project, listOf("implementation"), "quarkus-smallrye-opentracing")) {
                properties["quarkus.jaeger.metrics.enabled"] = "true"
            }
            if (DependencyUtils.hasDependency(project, listOf("implementation"), "smallrye-reactive-messaging-kafka")) {
                properties["quarkus.reactive-messaging.metrics.enabled"] = "true"
            }
            if (DependencyUtils.hasDependency(project, listOf("implementation"), "quarkus-smallrye-graphql")) {
                properties["quarkus.smallrye-graphql.metrics.enabled"] = "true"
            }
            if (DependencyUtils.hasOneOfDependency(project, listOf("implementation"), listOf("io.quarkus:quarkus-hibernate-orm-panache", "io.quarkus:quarkus-hibernate-orm-panache-kotlin"))) {
                properties["quarkus.hibernate-orm.metrics.enabled"] = "true"
            }

            // db
            if (DependencyUtils.hasOneOfDependency(project, listOf("implementation"), listOf("io.quarkus:quarkus-agroal", "io.quarkus:quarkus-hibernate-orm-panache", "io.quarkus:quarkus-hibernate-orm-panache-kotlin"))) {
                // batch
                properties["quarkus.hibernate-orm.jdbc.statement-batch-size"] = "300"
                properties["quarkus.hibernate-orm.fetch.batch-size"] = "100"

                // don't generate schema
                properties["quarkus.hibernate-orm.database.generation"] = "none"

                // default timeout
                properties["quarkus.transaction-manager.default-transaction-timeout"] = "30s"

                // log slow queries
                properties["quarkus.hibernate-orm.log.queries-slower-than-ms"] = "1000"
            }

            // db migrations
            if (config.frameworkDbMigrate.get()) {
                properties["quarkus.flyway.clean-disabled"] = "true"
                properties["quarkus.flyway.migrate-at-start"] = "true"
                properties["quarkus.flyway.baseline-on-migrate"] = "true"
            }

            // native image package config
            if (config.native.get()) {
                properties["quarkus.package.type"] = "native"
                properties["quarkus.native.container-build"] = "true"
                properties["quarkus.native.builder-image"] = "quay.io/quarkus/ubi-quarkus-native-image:21.3.0-java17"
                properties["quarkus.ssl.native"] = "true"
            } else {
                properties["quarkus.package.type"] = "fast-jar"
            }

            // manage file
            PluginHelper.createOrUpdatePropertyFile(project, project.file("src/main/resources/META-INF/microprofile-config.properties"), properties, managed = true)
        }
    }
}