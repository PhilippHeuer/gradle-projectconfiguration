package me.philippheuer.projectcfg.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.DependencyUtils
import me.philippheuer.projectcfg.util.HashUtils
import me.philippheuer.projectcfg.util.PluginLogger
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import java.net.URL

/**
 * Javadoc Module
 *
 * @param project the reference to the gradle project
 * @property config the global configuration of this plugin
 * @constructor Creates a new instance of this module
 */
class JavadocFeature constructor(override var project: Project, override var config: ProjectConfigurationExtension) : PluginModule {
    override fun check(): Boolean {
        return ProjectLanguage.JAVA == config.language.get()
    }

    override fun run() {
        // javadoc task
        if (project.subprojects.size == 0 || (project.subprojects.size != 0 && project.rootProject != project)) {
            configureJavadocTask(project, config)
            configureHtml5JDK9(project)
        }

        // javadoc aggregate task
        configureJavadocAggregateTask(project, config)
    }

    fun configureJavadocTask(project: Project, config: ProjectConfigurationExtension) {
        project.run {
            tasks.withType(Javadoc::class.java).configureEach {
                it.options.windowTitle = "${project.rootProject.name} (v${project.version}) - ${project.name}"
                PluginLogger.log(LogLevel.INFO, "set [tasks.javadoc.options.windowTitle] to [${it.options.windowTitle}]")
                it.options.encoding = "UTF-8"
                (it.options as StandardJavadocDocletOptions).docEncoding = "UTF-8"
                (it.options as StandardJavadocDocletOptions).charSet = "UTF-8"
                PluginLogger.log(LogLevel.INFO, "set [tasks.javadoc.options.encoding] to [${it.options.encoding}]")
                it.options.locale(config.javadocLocale.get())
                PluginLogger.log(LogLevel.INFO, "set [tasks.javadoc.options.locale] to [${config.javadocLocale.get()}]")

                // lint
                config.javadocLint.get().forEach { lint ->
                    (it.options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:$lint", true)
                }
                PluginLogger.log(LogLevel.INFO, "set [tasks.javadoc.options.doclint] to [${config.javadocLint.get().joinToString(",")}]")

                // links
                if (config.javadocLinks.get().size > 0) {
                    PluginLogger.log(LogLevel.INFO, "set [tasks.javadoc.options.links] to [${config.javadocLinks.get()}]")
                    (it.options as StandardJavadocDocletOptions).links(*config.javadocLinks.get().toTypedArray())
                }

                // javadoc auto-linking
                if (config.javadocAutoLinking.get()) {
                    DependencyUtils.getDependencies(it.project, listOf("implementation", "api", "default")).forEach { dep ->
                        // try to auto-link all external javadocs via javadoc.io
                        if (dep.version != null && !DependencyUtils.isProjectModule(project, dep)) {
                            val link = "https://javadoc.io/doc/${dep.group}/${dep.name}/${dep.version}"
                            val depHash = HashUtils.stringToSha256Hash("${dep.group}/${dep.name}/${dep.version}")
                            val checkCacheFile = project.file("$rootDir/build/javadocio-check/$depHash")

                            // check if javadoc exists using file (package-list, element-list)
                            var found = false
                            if (checkCacheFile.exists()) {
                                found = checkCacheFile.readText() == "true"
                            } else {
                                listOf("package-list", "element-list").forEach { file ->
                                    try {
                                        URL("$link/$file").openStream()
                                        found = true
                                    } catch (ex: Exception) {
                                        // ignore
                                    }
                                }
                            }

                            if (found) {
                                PluginLogger.log(LogLevel.DEBUG, "append [tasks.javadoc.options.links] element [${link}]")
                                (it.options as StandardJavadocDocletOptions).links?.add(link)
                            }
                            if (!checkCacheFile.exists()) {
                                checkCacheFile.parentFile.mkdirs()
                                checkCacheFile.writeText(found.toString())
                            }
                        }
                    }
                }

                // groups
                config.javadocGroups.get().run {
                    if (isNotEmpty()) {
                        values.distinct().forEach { groupName ->
                            PluginLogger.log(LogLevel.DEBUG, "set [tasks.javadoc.group.{$groupName}] to [${filter { e -> e.value == groupName }.map { e -> e.key}}]")
                            (it.options as StandardJavadocDocletOptions).group(groupName, filter { e -> e.value == groupName }.map { e -> e.key})
                        }
                    }
                }

                // custom templates
                if (config.javadocOverviewTemplate.isPresent) {
                    it.options.overview = file(config.javadocOverviewTemplate.get()).absolutePath
                }

                // others
                clearOutputFirst(it)
                jdk11ElementListBackwardsCompat(it, project)
            }
        }
    }

    fun configureHtml5JDK9(project: Project) {
        // html5 for jdk9+
        if (JavaVersion.current().isJava9Compatible) {
            PluginLogger.log(LogLevel.INFO, "set [tasks.javadoc.options.html5] to [true]")
            project.tasks.withType(Javadoc::class.java).configureEach {
                (it.options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
            }
        }
    }

    private fun jdk11ElementListBackwardsCompat(it: Javadoc, project: Project) {
        if (JavaVersion.current() >= JavaVersion.VERSION_11) {
            it.doLast(object : Action<Task> {
                // this can not be a lambda! see https://docs.gradle.org/7.3.1/userguide/validation_problems.html#implementation_unknown
                override fun execute(t: Task) {
                    project.copy { cp ->
                        cp.from(project.file("${it.destinationDir!!}/element-list"))
                        cp.into(it.destinationDir!!)
                        cp.rename { "package-list" }
                    }
                }
            })
        }
    }

    private fun clearOutputFirst(it: Javadoc) {
        it.doFirst(object : Action<Task> {
            // this can not be a lambda! see https://docs.gradle.org/7.3.1/userguide/validation_problems.html#implementation_unknown
            override fun execute(t: Task) {
                if (it.destinationDir?.exists() == true) {
                    it.destinationDir?.deleteRecursively()
                }
            }
        })
    }

    /**
     * configures a javadoc aggregation task, must be applied on the root project
     */
    fun configureJavadocAggregateTask(project: Project, config: ProjectConfigurationExtension) {
        if (project.rootProject == project && ProjectType.LIBRARY == config.type.get() && project.subprojects.size > 0) {
            project.run {
                project.tasks.register("aggregateJavadoc", Javadoc::class.java) { aj ->
                    aj.group = JavaBasePlugin.DOCUMENTATION_GROUP
                    aj.description = "Generates javadoc for all modules and merges them all together, useful to publish javadoc of all modules as documentation."
                    aj.setDestinationDir(project.file("${project.rootDir}/build/javadoc-aggregate"))

                    // sources
                    var javadocTasks = mutableListOf<Javadoc>()
                    subprojects.forEach { sp ->
                        sp.plugins.withType(JavaPlugin::class.java) {
                            val main = sp.extensions.getByType(JavaPluginExtension::class.java).sourceSets.getByName("main")
                            val javadoc = sp.tasks.named(main.javadocTaskName, Javadoc::class.java).get()

                            aj.dependsOn(sp.tasks.findByName(JavaPlugin.CLASSES_TASK_NAME))
                            javadocTasks.add(javadoc)
                        }
                    }
                    aj.source(javadocTasks.map { it.source })
                    aj.classpath = files(javadocTasks.map { it.classpath })

                    // lint
                    (aj.options as StandardJavadocDocletOptions).addStringOption("-Xdoclint:" + config.javadocLint.get().joinToString(","))

                    // custom templates
                    if (config.javadocOverviewAggregateTemplate.isPresent) {
                        aj.options.overview = project.file(config.javadocOverviewAggregateTemplate.get()).absolutePath
                    }

                    // merge options
                    subprojects.forEach { sp ->
                        sp.tasks.withType(Javadoc::class.java).configureEach { spj ->
                            // combine links
                            (spj.options as StandardJavadocDocletOptions).links?.forEach {
                                if ((spj.options as StandardJavadocDocletOptions).links!!.contains(it)) {
                                    (spj.options as StandardJavadocDocletOptions).links!!.add(it)
                                }
                            }

                            // combine offline links
                            (spj.options as StandardJavadocDocletOptions).linksOffline?.forEach {
                                if ((spj.options as StandardJavadocDocletOptions).linksOffline!!.contains(it)) {
                                    (spj.options as StandardJavadocDocletOptions).linksOffline!!.add(it)
                                }
                            }

                            // combine jflags
                            (spj.options as StandardJavadocDocletOptions).jFlags?.forEach {
                                if ((spj.options as StandardJavadocDocletOptions).jFlags!!.contains(it)) {
                                    (spj.options as StandardJavadocDocletOptions).jFlags!!.add(it)
                                }
                            }
                        }
                    }

                    // others
                    clearOutputFirst(aj)
                    jdk11ElementListBackwardsCompat(aj, project)
                }
            }
        }
    }
}
