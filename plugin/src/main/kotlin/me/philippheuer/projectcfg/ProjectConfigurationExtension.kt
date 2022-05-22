package me.philippheuer.projectcfg

import me.philippheuer.projectcfg.config.CheckstyleConfig
import me.philippheuer.projectcfg.config.FrameworkConfig
import me.philippheuer.projectcfg.config.GradleWrapperVersionConfig
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
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPom
import java.util.*
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
open class ProjectConfigurationExtension @Inject constructor(val project: Project) : PluginConfig, FrameworkConfig, JavaTypeConfig, LombokConfig, JavadocConfig, ShadowConfig, GradleWrapperVersionConfig, CheckstyleConfig {
    private val objects = project.objects

    override val logLevel: Property<LogLevel> = objects.property(LogLevel::class.java)
    override val language: Property<IProjectLanguage> = objects.property(IProjectLanguage::class.java).convention(ProjectLanguage.JAVA)
    override val javaVersion: Property<JavaVersion> = objects.property(JavaVersion::class.java).convention(JavaVersion.VERSION_11)
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

    override val frameworkWebApi: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val frameworkMetrics: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    override val frameworkTracing: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val frameworkDb: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val frameworkDbMigrate: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    override val lombokVersion: Property<String> = objects.property(String::class.java).convention("1.18.24")

    override val javadocEncoding: Property<String> = objects.property(String::class.java).convention("UTF-8")
    override val javadocLocale: Property<String> = objects.property(String::class.java).convention("en")
    override val javadocAutoLinking: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val javadocLinks: ListProperty<String> = objects.listProperty(String::class.java).convention(Collections.emptyList())
    override val javadocGroups: MapProperty<String, String> = objects.mapProperty(String::class.java, String::class.java).convention(mutableMapOf())
    override val javadocLombok: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    override val javadocOverviewTemplate: Property<String> = objects.property(String::class.java)
    override val javadocOverviewAggregateTemplate: Property<String> = objects.property(String::class.java)
    override val javadocLint: ListProperty<String> = objects.listProperty(String::class.java).convention(listOf("accessibility", "html", "reference", "syntax", "-missing"))

    override val shadow: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val shadowRelocate: Property<String> = objects.property(String::class.java)

    override val gradleVersionPolicyEnabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    override val checkstyleToolVersion: Property<String> = objects.property(String::class.java).convention("9.2.1")
    override val checkstyleRuleSet: Property<String> = objects.property(String::class.java).convention("")

    override fun toString(): String {
        return "LANGUAGE: ${language.orNull} - FRAMEWORK: ${framework.orNull}"
    }

    fun defaults() {
        // auto-detect project type
        if (type.get() == ProjectType.DEFAULT) {
            if (project.pluginManager.hasPlugin("java-library")) {
                type.set(ProjectType.LIBRARY)
            } else if (project.pluginManager.hasPlugin("java")) {
                type.set(ProjectType.APP)
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

    fun javaVersionAsJvmVersion(): String {
        if (javaVersion.get() == JavaVersion.VERSION_1_8) {
            return "1.8"
        }

        return javaVersion.get().toString()
    }

    fun javaVersionAsNumber(): Int {
        return javaVersion.get().majorVersion.toInt()
    }
}