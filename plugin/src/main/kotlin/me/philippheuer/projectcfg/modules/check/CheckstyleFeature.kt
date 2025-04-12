package me.philippheuer.projectcfg.modules.check

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.ProjectConfigurationPlugin
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.applyPlugin
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension

class CheckstyleFeature constructor(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return ctx.isProjectLanguage(ProjectLanguage.JAVA)
    }

    override fun run() {
        if (ctx.project.rootProject == ctx.project && (ctx.project.file("${ctx.project.rootDir}/checkstyle.xml").exists() || ctx.config.checkstyleRuleSet.orElse("").get().isNotEmpty())) {
            applyPlugin(ctx.project, ctx.config)
            reportingSetup(ctx.project)
        }
    }

    companion object {
        fun applyPlugin(project: Project, config: ProjectConfigurationExtension) {
            // plugin
            project.applyPlugin("checkstyle")

            // checkstyle
            project.tasks.register("checkstyleAll", Checkstyle::class.java) { task ->
                task.group = "verification"

                val mainSource = project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.getByName("main").java
                task.source = mainSource
                task.classpath = mainSource
                project.subprojects.filter { sp -> sp.pluginManager.hasPlugin("java") || sp.pluginManager.hasPlugin("java-library") }.forEach { sp ->
                    try {
                        val addSource = sp.extensions.getByType(JavaPluginExtension::class.java).sourceSets.getByName("main").java
                        task.source = task.source.plus(addSource)
                        task.classpath = task.classpath.plus(addSource)
                    } catch (ignored: Exception) {}
                }

                task.exclude("**/generated/**", "**/internal/**")
            }

            // configure
            project.extensions.run {
                configure(CheckstyleExtension::class.java) {
                    it.toolVersion = config.checkstyleToolVersion.get()

                    if (project.file("${project.rootDir}/checkstyle.xml").exists()) {
                        it.configFile = project.file("${project.rootDir}/checkstyle.xml")
                    } else if (config.checkstyleRuleSet.get().isNotEmpty()) {
                        val file = ProjectConfigurationPlugin::class.java.classLoader.getResource("checkstyle/${config.checkstyleRuleSet.get()}.xml")
                            ?: throw GradleException("checkstyle ruleset ${config.checkstyleRuleSet.get()} is not supported!")
                        PluginLogger.log(LogLevel.INFO, "using checkstyle ruleset [${config.checkstyleRuleSet.get()}]")
                        val targetFile = project.file("${project.buildDir}/tmp/checkstyle.xml")

                        val fileContent = file.readText()
                        targetFile.parentFile.mkdirs()
                        targetFile.writeText(fileContent)
                        it.configFile = targetFile
                    }
                    PluginLogger.log(LogLevel.INFO, "using checkstyle config [${it.configFile}]")

                    // fail-on-error
                    it.isIgnoreFailures = !config.strictChecks.get()
                    it.maxWarnings = 0
                    it.maxErrors = 0
                }
            }
        }

        fun reportingSetup(project: Project) {
            // tasks
            project.tasks.withType(Checkstyle::class.java).configureEach { task ->
                task.reports { report ->
                    report.xml.required.set(true)
                    report.html.required.set(true)
                    report.sarif.required.set(true)
                }
            }
        }
    }
}