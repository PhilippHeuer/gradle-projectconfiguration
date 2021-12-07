package io.github.philippheuer.gradleprojectsetup.features

import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.github.philippheuer.gradleprojectsetup.ProjectSetupExtension
import io.github.philippheuer.gradleprojectsetup.domain.PluginModule
import io.github.philippheuer.gradleprojectsetup.domain.ProjectLanguage
import io.github.philippheuer.gradleprojectsetup.domain.ProjectType
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.jvm.tasks.Jar

class ShadowFeature constructor(override var project: Project, override var config: ProjectSetupExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectLanguage.JAVA == config.language.get() && ProjectType.LIBRARY == config.type.get() && config.shadow.get()
    }

    override fun run() {
        log(LogLevel.INFO, "applying plugin [com.github.johnrengelman.shadow]")
        project.pluginManager.apply("com.github.johnrengelman.shadow")

        project.subprojects.forEach {
            log(LogLevel.INFO, "applying plugin [com.github.johnrengelman.shadow] to subproject [${it.displayName}]")
            it.pluginManager.apply("com.github.johnrengelman.shadow")
        }

        // relocate contents
        if (config.shadowRelocate.isPresent) {
            val relocateTask = project.tasks.create("relocateShadowJar", ConfigureShadowRelocation::class.java) {
                it.target = project.tasks.named("shadowJar", ShadowJar::class.java).get()
                it.prefix = config.shadowRelocate.get()
            }

            project.tasks.withType(Jar::class.java).configureEach {
                if (it is ShadowJar) {
                    it.dependsOn(relocateTask)
                    it.archiveClassifier.set("shaded")
                }
            }
        }
    }
}