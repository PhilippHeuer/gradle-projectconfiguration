package me.philippheuer.projectcfg.modules.framework.tasks

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.TaskUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*

abstract class QuarkusConfigurationTask : DefaultTask() {

    init {
        group = "projectcfg"
        description = "changes the default application properties embedded in the jar"
    }

    @get:Input
    lateinit var config: ProjectConfigurationExtension

    @TaskAction
    fun modifyApplicationPropertiesAction() {
        val configuration = getConfiguration()

        // add empty beans.xml to make quarkus scan classes in each library module
        val beansFile =  TaskUtils.getOutputResourcesFile(project, "META-INF/beans.xml")
        if (!beansFile.toFile().isFile) {
            beansFile.parent.toFile().mkdirs()
            beansFile.toFile().createNewFile()
        }

        // application.properties
        if (ProjectType.APP.valueEquals(config.type.get())) {
            val propertiesFile = TaskUtils.getOutputResourcesFile(project, "application.properties")
            if (propertiesFile.toFile().isFile) {
                processProperties(propertiesFile.toFile(), configuration)
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

        return properties
    }
}