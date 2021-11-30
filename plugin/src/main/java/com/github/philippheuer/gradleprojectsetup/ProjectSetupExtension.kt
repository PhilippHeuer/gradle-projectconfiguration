package com.github.philippheuer.gradleprojectsetup

import com.github.philippheuer.gradleprojectsetup.domain.ProjectFramework
import com.github.philippheuer.gradleprojectsetup.domain.ProjectLanguage
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
open class ProjectSetupExtension @Inject constructor(project: Project) {
    private val objects = project.objects

    // logLevel for debugging, if not set logs will be forwarded to slf4j
    val logLevel: Property<LogLevel> = objects.property(LogLevel::class.java)

    // language
    val language: Property<ProjectLanguage> = objects.property(ProjectLanguage::class.java).convention(ProjectLanguage.JAVA)

    // framework used in the project
    val framework: Property<ProjectFramework> = objects.property(ProjectFramework::class.java).convention(ProjectFramework.NONE)

    // enable / add prometheus components
    // val prometheus: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    override fun toString(): String {
        return "LANGUAGE: ${language.orNull} - FRAMEWORK: ${framework.orNull}"
    }
}