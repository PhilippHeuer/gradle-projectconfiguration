package me.philippheuer.projectcfg.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.PluginHelper
import me.philippheuer.projectcfg.util.addDepdenency
import me.philippheuer.projectcfg.util.applyProject
import org.gradle.api.Project
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

class SpringBootFramework constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectFramework.SPRINGBOOT.valueEquals(config.framework.get())
    }

    override fun run() {
        if (ProjectType.LIBRARY == config.type.get()) {
            configureLibrary(project, config)
        } else if (ProjectType.APP == config.type.get()) {
            configureApplication(project, config)
            configDefaults(project, config)
        }
    }

    companion object {
        fun configureLibrary(project: Project, config: ProjectConfigurationExtension) {
            project.run {
                // bom
                dependencies.enforcedPlatform("org.springframework.boot:spring-boot-dependencies:${DependencyVersion.springBootVersion}")

                // spring
                addDepdenency("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
                addDepdenency("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")
            }
        }

        fun configureApplication(project: Project, config: ProjectConfigurationExtension) {
            project.applyProject("org.springframework.boot")

            project.run {
                tasks.getByName("jar").enabled = false // disable jar task, this would generate a plain jar

                // bom
                dependencies.enforcedPlatform("org.springframework.boot:spring-boot-dependencies:${DependencyVersion.springBootVersion}")

                // spring
                addDepdenency("implementation", "org.springframework.boot:spring-boot-starter:${DependencyVersion.springBootVersion}")
                addDepdenency("testImplementation", "org.springframework.boot:spring-boot-starter-test:${DependencyVersion.springBootVersion}")

                // spring - log4j2
                configurations.getByName("implementation").exclude(mapOf("group" to "org.springframework.boot", "module" to "spring-boot-starter-logging"))
                addDepdenency("implementation", "org.springframework.boot:spring-boot-starter-log4j2:${DependencyVersion.springBootVersion}")

                // metrics
                if (config.frameworkMetrics.get()) {
                    addDepdenency("implementation", "io.micrometer:micrometer-core:1.8.1")
                    addDepdenency("implementation", "io.micrometer:micrometer-registry-prometheus:1.8.1")

                    // web project
                    if (DependencyUtils.hasDependency(project, listOf("implementation"), "org.springframework.boot:spring-boot-starter-web")) {
                        addDepdenency("implementation", "org.springframework.boot:spring-boot-starter-actuator:${DependencyVersion.springBootVersion}")
                    }
                }

                // native
                if (config.native.get()) {
                    project.applyProject("org.springframework.experimental.aot")

                    // repository and dependency
                    repositories.add(repositories.maven {
                        it.url = uri("https://repo.spring.io/release")
                    })
                    addDepdenency("implementation", "org.springframework.experimental:spring-native:${DependencyVersion.springNativeVersion}")

                    // task
                    tasks.withType(BootBuildImage::class.java).configureEach { image ->
                        image.builder = "paketobuildpacks/builder:tiny"
                        image.buildpacks = listOf("gcr.io/paketo-buildpacks/java-native-image:7.4.0")
                        image.environment = mapOf(
                            "BP_NATIVE_IMAGE" to "true"
                        )
                    }
                }
            }
        }

        fun configDefaults(project: Project, config: ProjectConfigurationExtension) {
            // see: https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html
            val properties = mutableMapOf(
                // banner
                "spring.main.banner-mode" to "off",

                // logging
                "logging.level.root" to "INFO",
                "logging.pattern.console" to "%d{yyyy-MM-dd HH:mm:ss} %highlight(%-5level) %logger{36} : %msg%n",
                "logging.pattern.file" to "%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} : %msg%n",
                "logging.charset.console" to "UTF-8",
                "logging.charset.file" to "UTF-8",

                // server
                "server.port" to "8080",

                // don't show default error page
                "server.error.whitelabel.enabled" to "false",

                // http2
                "server.http2.enabled" to "true",

                // graceful shutdown
                "server.shutdown" to "graceful",
                "spring.lifecycle.timeout-per-shutdown-phase" to "1m",

                // tomcat
                "server.tomcat.uri-encoding" to "UTF-8",
                "server.tomcat.relaxed-query-chars" to "[,]",

                // compression
                "server.compression.enabled" to "true",
                "server.compression.mime-types" to "text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json",
                "server.compression.min-response-size" to "1024",

                // cache
                "spring.web.resources.cache.cachecontrol.max-age" to "120",
                "spring.web.resources.cache.cachecontrol.must-revalidate" to "true",

                // spring
                "spring.main.allow-bean-definition-overriding" to "true",
            )

            // actuator
            if (DependencyUtils.hasDependency(project, listOf("implementation"), "org.springframework.boot:spring-boot-starter-actuator")) {
                // disable discovery
                properties["management.endpoints.web.discovery.enabled"] = "false"

                // use a different port for management endpoints
                properties["management.server.port"] = "8081"

                // expose endpoints
                var exposeEndpoints = mutableListOf("health", "heapdump", "prometheus")
                if (config.frameworkDbMigrate.get()) {
                    exposeEndpoints.add("flyway")
                }
                properties["management.endpoints.web.exposure.include"] = exposeEndpoints.joinToString(",")

                // expose /livez and /readyz and show more details
                properties["management.endpoint.health.probes.add-additional-paths"] = "true"
                properties["management.endpoint.health.show-details"] = "always"
            }

            // db migrations
            if (config.frameworkDbMigrate.get()) {
                properties["spring.flyway.baselineOnMigrate"] = "true"
                properties["spring.flyway.baselineVersion"] = "0"
                properties["spring.flyway.locations"] = "classpath:db/migration"
            }

            // manage file
            PluginHelper.createOrUpdatePropertyFile(project, project.file("src/main/resources/application-default.properties"), properties, managed = true)
        }
    }
}