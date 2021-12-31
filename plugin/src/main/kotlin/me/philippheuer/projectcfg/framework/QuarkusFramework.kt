package me.philippheuer.projectcfg.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.PluginLogger.Companion.config
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

                // metrics
                dependencies.add("implementation", "io.quarkus:quarkus-micrometer-registry-prometheus:${DependencyVersion.quarkusVersion}")

                // health
                dependencies.add("implementation", "io.quarkus:quarkus-smallrye-health:${DependencyVersion.quarkusVersion}")

                // rest
                dependencies.add("implementation", "io.quarkus:quarkus-resteasy:${DependencyVersion.quarkusVersion}")

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

                // test
                dependencies.add("testImplementation", "io.quarkus:quarkus-junit5:${DependencyVersion.quarkusVersion}")
            }
        }

        fun quarkusDefaults(project: Project) {
            val properties = mutableMapOf(
                // banner
                "quarkus.banner.enabled" to "false",

                // tune reload performance with instrumentation
                "quarkus.live-reload.instrumentation" to "true",

                // graceful exit
                "quarkus.shutdown.timeout" to "15",

                // http
                "quarkus.http.host" to "0.0.0.0",
                "quarkus.http.port" to "8080",

                // logging
                "quarkus.log.level" to "INFO",
                "quarkus.log.min-level" to "DEBUG",
                "quarkus.log.console.enabled" to "true",
                "quarkus.log.console.json" to "false",
                "quarkus.log.console.async" to "true",
                "quarkus.log.console.async.queue-length" to "512",

                // ui
                "quarkus.application.ui-header" to project.name,

                // healthcheck
                "quarkus.health.openapi.included" to "false",
                "quarkus.smallrye-health.ui.always-include" to "true",

                // native image package config
                "quarkus.package.type" to "native",
                "quarkus.native.container-build" to "true",
                "quarkus.native.builder-image" to "quay.io/quarkus/ubi-quarkus-native-image:21.3.0-java17",
                "quarkus.ssl.native" to "true",
            )
            val content = StringBuilder()
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
        quarkusDefaults(project)
    }
}