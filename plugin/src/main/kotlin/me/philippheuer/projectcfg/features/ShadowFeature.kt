package me.philippheuer.projectcfg.features

import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.jvm.tasks.Jar

class ShadowFeature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectLanguage.JAVA == config.language.get() && ProjectType.LIBRARY == config.type.get() && config.shadow.get()
    }

    override fun run() {
        applyShadowPlugin(project, config)
        configureShadowPlugin(project, config)
    }

    fun applyShadowPlugin(project: Project, config: ProjectConfigurationExtension) {
        log(LogLevel.INFO, "applying plugin [com.github.johnrengelman.shadow]")
        project.pluginManager.apply("com.github.johnrengelman.shadow")
    }

    fun configureShadowPlugin(project: Project, config: ProjectConfigurationExtension) {
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