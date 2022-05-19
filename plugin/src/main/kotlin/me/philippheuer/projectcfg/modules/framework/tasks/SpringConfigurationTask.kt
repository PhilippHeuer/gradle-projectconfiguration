package me.philippheuer.projectcfg.modules.framework.tasks

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.ProjectLibraries
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.TaskUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

abstract class SpringConfigurationTask : DefaultTask() {

    init {
        group = "projectcfg"
        description = "changes the default application properties embedded in the jar"
    }

    @get:Input
    lateinit var config: ProjectConfigurationExtension

    @TaskAction
    fun modifyApplicationPropertiesAction() {
        val configuration = getConfiguration()

        // application.properties
        val propertiesFile = TaskUtils.getOutputResourcesFile(project, "application.properties")
        if (propertiesFile.toFile().isFile) {
            TaskUtils.processProperties(propertiesFile.toFile(), configuration, false)
        }

        // system-default.properties
        val systemDefaultPropFile = TaskUtils.getOutputResourcesFile(project, "system-default.properties")
        if (!systemDefaultPropFile.toFile().isFile) {
            TaskUtils.processProperties(systemDefaultPropFile.toFile(), getSystemConfiguration(), true)
        }

        // use default log4j2.xml if nothing is provided
        val log4j2File = TaskUtils.getOutputResourcesFile(project, "log4j2.xml")
        if (!log4j2File.toFile().isFile && DependencyUtils.hasDependency(project, listOf("compileClasspath"), "org.apache.logging.log4j:log4j-core")) {
            var configFile = "logging/log4j2.xml"
            if (config.libraries.get().any { it.valueEquals(ProjectLibraries.SENTRYIO) }) {
                configFile = "logging/log4j2-sentry.xml"
            }

            javaClass.classLoader.getResourceAsStream(configFile).use {
                Files.copy(it!!, log4j2File)
            }
        }
    }

    private fun getConfiguration(): Map<String, String> {
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

            // graceful shutdown
            "server.shutdown" to "graceful",
            "spring.lifecycle.timeout-per-shutdown-phase" to "1m",
        )

        // actuator
        if (DependencyUtils.hasDependency(project, listOf("implementation"), "org.springframework.boot:spring-boot-starter-actuator")) {
            // discovery
            properties["management.endpoints.web.discovery.enabled"] = "true"

            // use a different port for management endpoints (this port should not be exposed to the outside)
            properties["management.server.port"] = "8081"

            // expose endpoints
            properties["management.endpoint.health.probes.add-additional-paths"] = "true"
            var exposeEndpoints = mutableListOf("info", "scheduledtasks", "beans", "caches", "conditions", "quartz", "loggers", "health", "heapdump", "threaddump", "prometheus")
            if (config.frameworkDbMigrate.get()) {
                exposeEndpoints.add("flyway")
            }
            properties["management.endpoints.web.exposure.include"] = exposeEndpoints.joinToString(",")

            // git details (provided by GitPropertiesFeature)
            properties["management.info.git.mode"] = "full"

            // expose /livez and /readyz and show more details
            properties["management.endpoint.health.probes.add-additional-paths"] = "true"
            properties["management.endpoint.health.show-details"] = "always"
        }

        // db migration
        if (config.frameworkDbMigrate.get()) {
            properties["spring.flyway.baselineOnMigrate"] = "true"
            properties["spring.flyway.baselineVersion"] = "0"
            properties["spring.flyway.locations"] = "classpath:db/migration"
        }

        return properties
    }

    private fun getSystemConfiguration(): Map<String, String> {
        val properties = mutableMapOf<String, String>()

        // log4j2 - see https://logging.apache.org/log4j/2.x/manual/async.html
        if (DependencyUtils.hasDependency(project, listOf("implementation"), "org.apache.logging.log4j:log4j-core")) {
            // - disable lookups
            properties["log4j2.formatMsgNoLookups"] = "true"
            // - lmax disruptor for async logging
            if (DependencyUtils.hasDependency(project, listOf("implementation"), "com.lmax:disruptor")) {
                properties["log4j2.contextSelector"] = "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
            }
        }

        return properties
    }
}