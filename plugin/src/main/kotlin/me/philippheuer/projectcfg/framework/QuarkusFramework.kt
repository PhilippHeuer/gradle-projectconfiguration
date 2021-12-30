package me.philippheuer.projectcfg.framework

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.DependencyVersion
import me.philippheuer.projectcfg.util.applyProject
import me.philippheuer.projectcfg.util.setDefaultProperty
import org.gradle.api.Project
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.model.AllOpen

class QuarkusFramework constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectFramework.QUARKUS == config.framework.get()
    }

    override fun run() {
        // plugin
        project.run {
            applyProject("io.quarkus")

            // properties
            setDefaultProperty("quarkus.package.type", "native")
            setDefaultProperty("quarkus.native.container-build", "true")
            setDefaultProperty("quarkus.native.builder-image", "quay.io/quarkus/ubi-quarkus- KotlinPlatformType.native -image:21.3.0-java17")

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
}