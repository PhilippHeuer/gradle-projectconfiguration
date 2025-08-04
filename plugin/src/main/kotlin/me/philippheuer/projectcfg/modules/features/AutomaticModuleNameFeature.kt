package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaPlugin
import org.gradle.jvm.tasks.Jar

/**
 * Automatic Module Name for Java 9+ Module Support
 *
 * @see <a href="https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_modular_auto">Gradle Java Library Plugin - Automatic Module Name</a>
 */
class AutomaticModuleNameFeature(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        if (!ctx.project.isRootProjectWithoutSubprojectsOrSubproject()) {
            return false
        }
        if (!ctx.isProjectType(ProjectType.LIBRARY)) {
            return false
        }
        if (!ctx.isProjectLanguage(ProjectLanguage.JAVA) && !ctx.isProjectLanguage(ProjectLanguage.KOTLIN)) {
            return false
        }
        if (ctx.project.pluginManager.hasPlugin("java-platform") || ctx.project.pluginManager.hasPlugin("version-catalog")) {
            return false
        }

        return !ctx.project.file("src/main/java/module-info.java").exists()
    }

    override fun run() {
        configureAutoModuleName(ctx.project)
    }

    companion object {
        private fun configureAutoModuleName(project: Project) {
            val rootName = project.rootProject.name.replace('-', '.')
            val currentName = project.name.replace('-', '.')
            val moduleName = if (project != project.rootProject) {
                "$rootName.$currentName"
            } else {
                currentName
            }

            PluginLogger.log(LogLevel.INFO, "setting manifest attr [Automatic-Module-Name] to [$moduleName]")
            project.plugins.withType(JavaPlugin::class.java) {
                project.tasks.withType(Jar::class.java).configureEach { jar ->
                    jar.manifest { mf ->
                        mf.attributes(mapOf("Automatic-Module-Name" to moduleName))
                    }
                }
            }
        }
    }
}
