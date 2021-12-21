package me.philippheuer.projectcfg.features

import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectLanguage
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.ProjectConfigurationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions

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
        project.run {
            // global options
            tasks.withType(Javadoc::class.java).configureEach {
                it.options.windowTitle = "${project.rootProject.name} (v${project.version})"
                log(LogLevel.INFO, "set [tasks.javadoc.options.windowTitle] to [${it.options.windowTitle}]")
                it.options.encoding = "UTF-8"
                (it.options as StandardJavadocDocletOptions).docEncoding = "UTF-8"
                (it.options as StandardJavadocDocletOptions).charSet = "UTF-8"
                log(LogLevel.INFO, "set [tasks.javadoc.options.encoding] to [${it.options.encoding}]")
                it.options.locale(config.javadocLocale.get())
                log(LogLevel.INFO, "set [tasks.javadoc.options.locale] to [${config.javadocLocale.get()}]")

                // lint
                (it.options as StandardJavadocDocletOptions).addStringOption("-Xdoclint:" + config.javadocLint.get().joinToString(","))
                log(LogLevel.INFO, "set [tasks.javadoc.options.doclint] to [${config.javadocLint.get().joinToString(",")}]")

                // links
                if (config.javadocLinks.get().size > 0) {
                    log(LogLevel.INFO, "set [tasks.javadoc.options.links] to [${config.javadocLinks.get()}]")
                    (it.options as StandardJavadocDocletOptions).links(*config.javadocLinks.get().toTypedArray())
                }

                // groups
                config.javadocGroups.get().run {
                    if (isNotEmpty()) {
                       values.distinct().forEach { groupName ->
                           log(LogLevel.INFO, "set [tasks.javadoc.group.{$groupName}] to [${filter { e -> e.value == groupName }.map { e -> e.key}}]")
                           (it.options as StandardJavadocDocletOptions).group(groupName, filter { e -> e.value == groupName }.map { e -> e.key})
                        }
                    }
                }

                // custom templates
                if (config.javadocOverviewTemplate.isPresent) {
                    it.options.overview = file(config.javadocOverviewTemplate.get()).absolutePath
                }

                // JDK11 fix - copy element-list to package-list
                if (JavaVersion.current() >= JavaVersion.VERSION_11) {
                    it.doLast { _ ->
                        copy { cp ->
                            cp.from(file("${it.destinationDir!!}/element-list"))
                            cp.into(it.destinationDir!!)
                            cp.rename { "package-list" }
                        }
                    }
                }
            }

            // html5 for jdk9+
            log(LogLevel.DEBUG, "using jdk9 or later, javadoc supports html5 output")
            if (JavaVersion.current().isJava9Compatible) {
                log(LogLevel.INFO, "set [tasks.javadoc.options.html5] to [true]")
                tasks.withType(Javadoc::class.java).configureEach {
                    (it.options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
                }
            }

            // merge javadoc (library only)
            log(LogLevel.INFO, "task allJavadoc: condition [type = ${ProjectType.LIBRARY}] is [${ProjectType.LIBRARY == config.type.get()}]")
            log(LogLevel.INFO, "task allJavadoc: condition [subprojects.size > 0] is [${subprojects.size > 0}]")
            if (ProjectType.LIBRARY == config.type.get() && subprojects.size > 0) {
                tasks.register("aggregateJavadoc", Javadoc::class.java) { aj ->
                    aj.group = JavaBasePlugin.DOCUMENTATION_GROUP
                    aj.description = "Generates javadoc for all modules and merges them all together, useful to publish javadoc of all modules as documentation."
                    aj.setDestinationDir(file("${rootDir}/build/javadoc-aggregate"))

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
                        aj.options.overview = file(config.javadocOverviewAggregateTemplate.get()).absolutePath
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

                    // clear old files
                    aj.doFirst {
                        if (aj.destinationDir?.exists() == true) {
                            aj.destinationDir?.deleteRecursively()
                        }
                    }

                    // JDK11 fix - copy element-list to package-list
                    if (JavaVersion.current() >= JavaVersion.VERSION_11) {
                        aj.doLast {
                            copy {
                                it.from(file("${aj.destinationDir!!}/element-list"))
                                it.into(aj.destinationDir!!)
                                it.rename { "package-list" }
                            }
                        }
                    }
                }
            }
        }
    }

}
