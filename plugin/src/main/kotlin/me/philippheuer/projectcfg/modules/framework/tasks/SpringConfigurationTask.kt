package me.philippheuer.projectcfg.modules.framework.tasks

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.TaskUtils
import me.philippheuer.projectcfg.util.addDependency
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import java.util.*

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
            processProperties(propertiesFile.toFile(), configuration)
        }

        // use default log4j2.xml if nothing is provided
        val log4j2File = TaskUtils.getOutputResourcesFile(project, "log4j2.xml")
        if (!log4j2File.toFile().isFile && DependencyUtils.hasDependency(project, listOf("compileClasspath"), "org.apache.logging.log4j:log4j-core")) {
            javaClass.classLoader.getResourceAsStream("logging/log4j2.xml").use {
                Files.copy(it, log4j2File)
            }
        }
    }

    private fun processProperties(file: File, defaultProperties: Map<String, String>) {
        val prop = Properties()
        defaultProperties.forEach { (key, value) -> prop.setProperty(key, value) }
        prop.load(file.bufferedReader())
        prop.store(file.bufferedWriter(), null)
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

        // db migration
        if (config.frameworkDbMigrate.get()) {
            properties["spring.flyway.baselineOnMigrate"] = "true"
            properties["spring.flyway.baselineVersion"] = "0"
            properties["spring.flyway.locations"] = "classpath:db/migration"
        }

        return properties
    }
}