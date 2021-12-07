package me.philippheuer.projectcfg.features

import io.freefair.gradle.plugins.lombok.LombokExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions

class LombokFeature constructor(override var project: Project, override var config: me.philippheuer.projectcfg.ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectLanguage.JAVA == config.language.get()
    }

    override fun run() {
        log(LogLevel.INFO, "applying plugin [io.freefair.lombok]")
        project.pluginManager.apply("io.freefair.lombok")

        project.extensions.configure(LombokExtension::class.java) {
            it.disableConfig.set(true) // don't generate lombok.config files
            log(LogLevel.INFO, "set [lombok.disableConfig] to [${it.disableConfig}]")
            it.version.set("1.18.22")
            log(LogLevel.INFO, "set [lombok.version] to [${it.version}]")
        }

        log(LogLevel.INFO, "option [javadocLombok] is [${config.javadocLombok.get()}]")
        if (config.javadocLombok.get()) {
            // javadoc - delombok
            val delombok = project.tasks.getByName("delombok")
            project.tasks.withType(Javadoc::class.java).configureEach {
                log(LogLevel.INFO, "set [tasks.javadoc.source] to [delombok]")
                it.source(delombok)
                log(LogLevel.INFO, "set [tasks.javadoc.dependsOn] to [delombok]")
                it.dependsOn(delombok)
            }

            // delombok as source would generate a lot of unfixable javadoc warnings
            log(LogLevel.INFO, "set [tasks.javadoc.options.Xdoclint:none] to [-quiet]")
            project.tasks.withType(Javadoc::class.java).configureEach {
                (it.options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
            }
        }

        project.subprojects.forEach {
            log(LogLevel.INFO, "applying plugin [io.freefair.lombok] to subproject [${it.displayName}]")
            it.pluginManager.apply("io.freefair.lombok")
        }
    }
}