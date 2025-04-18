package me.philippheuer.projectcfg.config

import me.philippheuer.projectcfg.domain.IProjectFramework
import me.philippheuer.projectcfg.domain.IProjectLanguage
import me.philippheuer.projectcfg.domain.IProjectType
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPom

interface PluginConfig {

    // logLevel for debugging, if not set logs will be forwarded to slf4j
    val logLevel: Property<LogLevel>

    // language
    val language: Property<IProjectLanguage>

    // type
    val type: Property<IProjectType>

    // framework used in the project
    val framework: Property<IProjectFramework>

    // file encoding used in the project
    val fileEncoding: Property<String>

    // repository to publish library artifacts in
    val artifactRepository: Property<ArtifactRepository>

    // artifact group for publications
    val artifactGroupId: Property<String>

    // artifact id for publications
    val artifactId: Property<String>

    // artifact version for publications
    val artifactVersion: Property<String>

    // artifact display name
    val artifactDisplayName: Property<String>

    // artifact description
    val artifactDescription: Property<String>

    // customize pom
    var pom: (MavenPom) -> Unit

    // build native binary
    val native: Property<Boolean>

    /**
     * Determines whether common linting check failures should fail the entire build.
     *
     * @since 0.16.1
     * @default true, or false in CI
     */
    val strictChecks: Property<Boolean>

    /**
     * List of plugin modules that should not be applied.
     */
    val disablePluginModules: ListProperty<String>

    /**
     * postProcess can be used to apply config changes / set defaults, after the user configuration has been parsed
     */
    fun postProcess()
}