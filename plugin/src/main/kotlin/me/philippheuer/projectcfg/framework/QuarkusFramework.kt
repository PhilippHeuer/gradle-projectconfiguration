package me.philippheuer.projectcfg.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.PluginLogger.Companion.config
import me.philippheuer.projectcfg.util.PluginLogger.Companion.project
import me.philippheuer.projectcfg.util.applyProject
import org.gradle.api.Project
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import java.io.File

class QuarkusFramework constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    companion object {
        fun applyPlugin(project: Project, config: ProjectConfigurationExtension) {
            // plugin
            project.run {
                // plugin
                applyProject("io.quarkus")

                // bom
                dependencies.enforcedPlatform("io.quarkus.platform:quarkus-bom:${DependencyVersion.quarkusVersion}")

                // health
                dependencies.add("implementation", "io.quarkus:quarkus-smallrye-health:${DependencyVersion.quarkusVersion}")

                // cache
                dependencies.add("implementation", "io.quarkus:quarkus-cache:${DependencyVersion.quarkusVersion}")

                // logging
                dependencies.add("implementation", "org.jboss.slf4j:slf4j-jboss-logmanager:1.1.0.Final")
                dependencies.add("implementation", "io.quarkus:quarkus-logging-json:${DependencyVersion.quarkusVersion}")

                // kotlin
                if (config.language.get() == ProjectLanguage.KOTLIN) {
                    applyProject("org.jetbrains.kotlin.plugin.allopen")

                    dependencies.add("implementation", "io.quarkus:quarkus-kotlin:${DependencyVersion.quarkusVersion}")

                    extensions.configure(AllOpenExtension::class.java) {
                        it.annotation("io.quarkus.test.junit.QuarkusTest")
                        it.annotation("javax.enterprise.context.ApplicationScoped")
                    }
                }

                // rest
                if (config.frameworkWebApi.get()) {
                    dependencies.add("implementation", "io.quarkus:quarkus-resteasy:${DependencyVersion.quarkusVersion}")
                }

                // metrics
                if (config.frameworkMetrics.get()) {
                    dependencies.add("implementation", "io.quarkus:quarkus-micrometer-registry-prometheus:${DependencyVersion.quarkusVersion}")
                }

                // tracing
                if (config.frameworkTracing.get()) {
                    dependencies.add("implementation", "io.quarkus:quarkus-smallrye-opentracing:${DependencyVersion.quarkusVersion}")
                }

                // db
                if (config.frameworkDb.get()) {
                    if (config.language.get() == ProjectLanguage.JAVA) {
                        dependencies.add("implementation", "io.quarkus:quarkus-hibernate-orm-panache:${DependencyVersion.quarkusVersion}")
                    } else if (config.language.get() == ProjectLanguage.KOTLIN) {
                        dependencies.add("implementation", "io.quarkus:quarkus-hibernate-orm-panache-kotlin:${DependencyVersion.quarkusVersion}")
                    }
                }

                // db migrations
                if (config.frameworkDbMigrate.get()) {
                    dependencies.add("implementation", "io.quarkus:quarkus-flyway:${DependencyVersion.quarkusVersion}")
                }

                // test
                dependencies.add("testImplementation", "io.quarkus:quarkus-junit5:${DependencyVersion.quarkusVersion}")

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

        fun quarkusDefaults(config: ProjectConfigurationExtension) {
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
                "quarkus.log.console.enabled" to "true",
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
            properties["quarkus.hibernate-orm.metrics.enabled"] = "true"
            properties["quarkus.reactive-messaging.metrics.enabled"] = "true"
            properties["quarkus.smallrye-graphql.metrics.enabled"] = "true"
            if (DependencyUtils.hasDependency(project, listOf("implementation"), "io.quarkus:quarkus-agroal")) {
                properties["quarkus.datasource.metrics.enabled"] = "true"
            }
            if (DependencyUtils.hasDependency(project, listOf("implementation"), "io.quarkus:quarkus-scheduler")) {
                properties["quarkus.scheduler.metrics.enabled"] = "true"
            }
            if (DependencyUtils.hasDependency(project, listOf("implementation"), "quarkus-smallrye-opentracing")) {
                properties["quarkus.jaeger.metrics.enabled"] = "true"
            }

            // db migrations
            if (config.frameworkDbMigrate.get()) {
                properties["quarkus.flyway.clean-disabled"] = "true"
                properties["quarkus.flyway.migrate-at-start"] = "true"
                properties["quarkus.flyway.baseline-on-migrate"] = "true"
            }

            // native image package config
            properties["quarkus.package.type"] = "native"
            properties["quarkus.native.container-build"] = "true"
            properties["quarkus.native.builder-image"] = "quay.io/quarkus/ubi-quarkus-native-image:21.3.0-java17"
            properties["quarkus.ssl.native"] = "true"

            // write config
            val content = StringBuilder()
            content.append("# DO NOT EDIT! This file contains defaults and is managed automatically generated by the me.philippheuer.projectcfg gradle plugin.\n")
            properties.forEach {
                content.append("${it.key}=${it.value}\n")
            }
            val defaultPropertyFile = File("src/main/resources/META-INF/microprofile-config.properties")
            defaultPropertyFile.parentFile.mkdirs()
            defaultPropertyFile.writeText(content.toString())
        }
    }

    override fun check(): Boolean {
        return ProjectFramework.QUARKUS == config.framework.get()
    }

    override fun run() {
        applyPlugin(project, config)
        quarkusDefaults(config)
    }
}