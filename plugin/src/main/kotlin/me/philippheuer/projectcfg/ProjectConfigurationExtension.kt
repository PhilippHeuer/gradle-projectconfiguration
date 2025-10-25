package me.philippheuer.projectcfg

import me.philippheuer.projectcfg.config.CheckstyleConfig
import me.philippheuer.projectcfg.config.DokkaConfig
import me.philippheuer.projectcfg.config.FrameworkConfig
import me.philippheuer.projectcfg.config.GradleWrapperVersionConfig
import me.philippheuer.projectcfg.config.JacocoConfig
import me.philippheuer.projectcfg.config.JavaTypeConfig
import me.philippheuer.projectcfg.config.JavadocConfig
import me.philippheuer.projectcfg.config.LombokConfig
import me.philippheuer.projectcfg.config.PluginConfig
import me.philippheuer.projectcfg.config.ShadowConfig
import me.philippheuer.projectcfg.domain.IProjectFramework
import me.philippheuer.projectcfg.domain.IProjectLanguage
import me.philippheuer.projectcfg.domain.IProjectType
import me.philippheuer.projectcfg.domain.ProjectFramework
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.TaskUtils
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPom
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.jetbrains.dokka.gradle.DokkaExtension
import java.util.Collections
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
open class ProjectConfigurationExtension @Inject constructor(val project: Project) : PluginConfig, FrameworkConfig, JavaTypeConfig, LombokConfig, JavadocConfig, DokkaConfig, ShadowConfig, GradleWrapperVersionConfig, CheckstyleConfig, JacocoConfig {
    private val objects = project.objects

    override val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    override val logLevel: Property<LogLevel> = objects.property(LogLevel::class.java).convention(LogLevel.INFO)
    override val language: Property<IProjectLanguage> = objects.property(IProjectLanguage::class.java).convention(ProjectLanguage.DEFAULT)
    override val javaVersion: Property<JavaVersion> = objects.property(JavaVersion::class.java).convention(JavaVersion.VERSION_11)
    override val javaToolchainVersion: Property<JavaVersion> = objects.property(JavaVersion::class.java)
    override val type: Property<IProjectType> = objects.property(IProjectType::class.java).convention(ProjectType.DEFAULT)
    override val framework: Property<IProjectFramework> = objects.property(IProjectFramework::class.java).convention(ProjectFramework.NONE)
    override val fileEncoding: Property<String> = objects.property(String::class.java).convention("UTF-8")
    override val artifactRepository: Property<ArtifactRepository> = objects.property(ArtifactRepository::class.java)
    override val artifactGroupId: Property<String> = objects.property(String::class.java)
    override val artifactId: Property<String> = objects.property(String::class.java)
    override val artifactVersion: Property<String> = objects.property(String::class.java)
    override val artifactDisplayName: Property<String> = objects.property(String::class.java)
    override val artifactDescription: Property<String> = objects.property(String::class.java)
    override var pom: (MavenPom) -> Unit = {}
    override val native: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val strictChecks: Property<Boolean> = objects.property(Boolean::class.java).convention(!TaskUtils.isCI())
    override val disablePluginModules: ListProperty<String> = objects.listProperty(String::class.java).convention(Collections.emptyList())

    override val frameworkWebApi: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val frameworkMetrics: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    override val frameworkTracing: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val frameworkDb: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val frameworkDbMigrate: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    // renovate: datasource=maven depName=org.projectlombok:lombok
    override val lombokVersion: Property<String> = objects.property(String::class.java).convention("1.18.42")

    override val javadocTitle: Property<String> = objects.property(String::class.java)
    override val javadocEncoding: Property<String> = objects.property(String::class.java).convention("UTF-8")
    override val javadocLocale: Property<String> = objects.property(String::class.java).convention("en_US")
    override val javadocAutoLinking: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val javadocLinks: ListProperty<String> = objects.listProperty(String::class.java).convention(Collections.emptyList())
    override val javadocGroups: MapProperty<String, String> = objects.mapProperty(String::class.java, String::class.java).convention(mutableMapOf())
    override val javadocLombok: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    override val javadocOverviewTemplate: Property<String> = objects.property(String::class.java)
    override val javadocOverviewAggregateTemplate: Property<String> = objects.property(String::class.java)
    override val javadocLint: ListProperty<String> = objects.listProperty(String::class.java).convention(listOf("accessibility", "html", "reference", "syntax", "-missing"))
    override var javadocCustomize: (StandardJavadocDocletOptions) -> Unit = {}
    override var javadocAggregateCustomize: (StandardJavadocDocletOptions) -> Unit = {}

    override var dokka: (DokkaExtension) -> Unit = {}

    override val shadow: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val shadowRelocate: Property<String> = objects.property(String::class.java)

    override val gradleVersionPolicyEnabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    // renovate: datasource=maven depName=org.jacoco:org.jacoco.core
    override val jacocoVersion: Property<String> = objects.property(String::class.java).convention("0.8.14")

    // renovate: datasource=maven depName=com.puppycrawl.tools:checkstyle
    override val checkstyleToolVersion: Property<String> = objects.property(String::class.java).convention("12.1.0")
    override val checkstyleRuleSet: Property<String> = objects.property(String::class.java).convention("")

    override fun toString(): String {
        return "LANGUAGE: ${language.orNull} - FRAMEWORK: ${framework.orNull}"
    }

    override fun postProcess() {
        PluginLogger.setContext(project, null)

        // auto-detect project type
        if (type.get() == ProjectType.DEFAULT) {
            if (project.pluginManager.hasPlugin("java-library") || project.pluginManager.hasPlugin("java-platform")) {
                type.set(ProjectType.LIBRARY)
            } else if (project.pluginManager.hasPlugin("java") || project.pluginManager.hasPlugin("application")) {
                type.set(ProjectType.APP)
            }

            if (type.get() != ProjectType.DEFAULT) {
                PluginLogger.log(LogLevel.INFO, "Set project type to [${type.get()}]")
            }
        }

        // auto-detect project language
        if (language.get() == ProjectLanguage.DEFAULT) {
            if (project.pluginManager.hasPlugin("org.jetbrains.kotlin.jvm") || project.pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
                language.set(ProjectLanguage.KOTLIN)
            } else {
                language.set(ProjectLanguage.JAVA)
            }

            if (language.get() != ProjectLanguage.DEFAULT) {
                PluginLogger.log(LogLevel.INFO, "Set project language to [${language.get()}]")
            }
        }

        // auto-detect project framework
        if (framework.get() == ProjectFramework.NONE) {
            if (project.pluginManager.hasPlugin("org.springframework.boot")) {
                framework.set(ProjectFramework.SPRINGBOOT)
            } else if (project.pluginManager.hasPlugin("io.quarkus")) {
                framework.set(ProjectFramework.QUARKUS)
            }

            if (framework.get() != ProjectFramework.NONE) {
                PluginLogger.log(LogLevel.INFO, "Set project framework to [${framework.get()}]")
            }
        }

        // artifact
        if (!artifactGroupId.isPresent) {
            if (project.properties["artifact.group"] is String) {
                artifactGroupId.set(project.properties["artifact.group"] as String)
            } else if (project.properties["group"] is String) {
                artifactGroupId.set(project.properties["group"] as String)
            } else {
                artifactGroupId.set(project.group as String?)
            }
        }
        if (!artifactId.isPresent) {
            artifactId.set(project.name)
        }
        if (!artifactVersion.isPresent) {
            if (project.properties["artifact.version"] is String) {
                artifactVersion.set(project.properties["artifact.version"] as String)
            } else if (project.properties["version"] is String) {
                artifactVersion.set(project.properties["version"] as String)
            } else if (project.version != "undefined") {
                artifactVersion.set(project.version as String?)
            }
        }
    }
}
