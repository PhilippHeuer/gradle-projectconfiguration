package com.github.philippheuer.gradleprojectsetup.features

import com.coditory.gradle.manifest.ManifestPluginExtension
import com.github.philippheuer.gradleprojectsetup.ProjectSetupExtension
import com.github.philippheuer.gradleprojectsetup.domain.PluginModule
import com.github.philippheuer.gradleprojectsetup.domain.ProjectLanguage
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.jvm.tasks.Jar
import java.io.File

class ManifestFeature constructor(override var project: Project, override var config: ProjectSetupExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectLanguage.JAVA == config.language.get()
    }

    override fun run() {
        log(LogLevel.INFO, "applying plugin [com.coditory.manifest]")
        project.pluginManager.apply("com.coditory.manifest")

        project.subprojects.forEach {
            log(LogLevel.INFO, "applying plugin [com.coditory.manifest] to subproject [${it.displayName}]")
            it.pluginManager.apply("com.coditory.manifest")
        }

        project.extensions.configure(ManifestPluginExtension::class.java) {
            it.buildAttributes = false
            log(LogLevel.INFO, "set [manifest.buildAttributes] to [${it.buildAttributes}]")
        }

        project.tasks.withType(Jar::class.java).configureEach {
            it.dependsOn(project.tasks.getByName("manifest"))
            it.manifest.from(File(project.buildDir, "resources/main/META-INF/MANIFEST.MF"))
        }
    }
}