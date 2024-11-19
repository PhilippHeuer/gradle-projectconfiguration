package me.philippheuer.projectcfg.spring.springframework

import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.PropertyUtils
import me.philippheuer.projectcfg.util.TaskUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class SpringApplicationProperties : DefaultTask() {
    init {
        group = "other"
        description = "modifies the application properties, can enforce or set absent properties"
    }

    @TaskAction
    fun modifyApplicationPropertiesAction() {
        // application.yaml
        val propertyFiles = listOf(
            TaskUtils.getOutputResourcesFile(project, "application.yaml"),
            TaskUtils.getOutputResourcesFile(project, "application.yml")
        )
        propertyFiles.map { it.toFile() }.filter { it.isFile }.forEach { file ->
            PropertyUtils.processYamlProperties(file, getConfiguration(), false)
            PropertyUtils.processYamlProperties(file, getSystemConfiguration(), true)
        }

        // application.properties
        val propertiesFile = TaskUtils.getOutputResourcesFile(project, "application.properties")
        if (propertiesFile.toFile().isFile) {
            PropertyUtils.processProperties(propertiesFile.toFile(), getConfiguration(), false)
            PropertyUtils.processProperties(propertiesFile.toFile(), getSystemConfiguration(), true)
        }
    }

    /**
     * The configuration properties will be set, if the project did not define them.
     */
    private fun getConfiguration(): Map<String, String> {
        // see: https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html
        val properties = mutableMapOf(
            // app
            "spring.application.name" to project.name,

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

            // graceful shutdown
            "server.shutdown" to "graceful",
            "spring.lifecycle.timeout-per-shutdown-phase" to "30s",

            // dev tools
            "spring.devtools.add-properties" to "true",
            "spring.devtools.restart.poll-interval" to "2s",
            "spring.devtools.restart.quiet-period" to "1s",
        )

        return properties
    }

    /**
     * The system configuration properties will overwrite the project defined properties, useful to enforce certain settings.
     */
    private fun getSystemConfiguration(): Map<String, String> {
        val properties = mutableMapOf<String, String>()

        // log4j2
        if (DependencyUtils.hasDependency(project, listOf("implementation"), "org.apache.logging.log4j:log4j-core")) {
            properties["log4j2.formatMsgNoLookups"] = "true" // disable lookups
        }

        return properties
    }
}
