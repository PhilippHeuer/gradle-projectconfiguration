package me.philippheuer.projectcfg

import me.philippheuer.projectcfg.config.*
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
import java.util.*
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
open class ProjectConfigurationExtension @Inject constructor(project: Project) : PluginConfig, FrameworkConfig, JavaTypeConfig, LombokConfig, JavadocConfig, ShadowConfig, GradleWrapperVersionConfig {
    private val objects = project.objects

    override val logLevel: Property<LogLevel> = objects.property(LogLevel::class.java)
    override val language: Property<ProjectLanguage> = objects.property(ProjectLanguage::class.java).convention(ProjectLanguage.JAVA)
    override val javaVersion: Property<JavaVersion> = objects.property(JavaVersion::class.java).convention(JavaVersion.VERSION_11)
    override val type: Property<ProjectType> = objects.property(ProjectType::class.java).convention(ProjectType.APP)
    override val framework: Property<ProjectFramework> = objects.property(ProjectFramework::class.java).convention(ProjectFramework.NONE)
    override val fileEncoding: Property<String> = objects.property(String::class.java).convention("UTF-8")
    override val artifactRepository: Property<ArtifactRepository> = objects.property(ArtifactRepository::class.java)
    override val artifactGroupId: Property<String> = objects.property(String::class.java)
    override val artifactId: Property<String> = objects.property(String::class.java)

    override val frameworkMetrics: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    override val lombokVersion: Property<String> = objects.property(String::class.java).convention("1.18.22")

    override val javadocEncoding: Property<String> = objects.property(String::class.java).convention("UTF-8")
    override val javadocLocale: Property<String> = objects.property(String::class.java).convention("en")
    override val javadocLinks: ListProperty<String> = objects.listProperty(String::class.java).convention(Collections.emptyList())
    override val javadocGroups: MapProperty<String, String> = objects.mapProperty(String::class.java, String::class.java).convention(mutableMapOf())
    override val javadocLombok: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    override val javadocOverviewTemplate: Property<String> = objects.property(String::class.java)
    override val javadocOverviewAggregateTemplate: Property<String> = objects.property(String::class.java)
    override val javadocLint: ListProperty<String> = objects.listProperty(String::class.java).convention(listOf("accessibility", "html", "reference", "syntax"))

    override val shadow: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val shadowRelocate: Property<String> = objects.property(String::class.java)

    override val gradleVersionPolicyEnabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    // enable / add prometheus components
    // val prometheus: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    override fun toString(): String {
        return "LANGUAGE: ${language.orNull} - FRAMEWORK: ${framework.orNull}"
    }
}