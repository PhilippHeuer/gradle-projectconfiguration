package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
import me.philippheuer.projectcfg.util.isRootProject
import me.philippheuer.projectcfg.util.isRootProjectWithSubprojects
import me.philippheuer.projectcfg.util.isRootProjectWithoutSubprojectsOrSubproject
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Zip

/**
 * Task to create a reusable publication bundle as a ZIP archive.
 * <p>
 * This task publishes the library into a temporary local Maven repository.
 * Once all artifacts have been written to this local repository, they are packaged into a single ZIP archive.
 * The resulting archive follows the Maven Repository Layout and is placed in the {@code build/distributions} directory.
 *
 * @see <a href="https://central.sonatype.org/publish/publish-portal-upload/#publishing-by-uploading-a-bundle">publishing to sonatype portal by uploading a bundle</a>
 */
class PublicationBundleFeature(override var ctx: IProjectContext) : PluginModule {
    override fun check(): Boolean {
        return true
    }

    override fun run() {
        if (ctx.project.isRootProjectWithoutSubprojectsOrSubproject() && ctx.isProjectType(ProjectType.LIBRARY)) {
            configureBundle(ctx.project, ctx.config)
        }

        // not perfect, but there doesn't seem to be a easy way to access the config for a subproject
        if (ctx.project.isRootProjectWithSubprojects() && ctx.project.subprojects.isNotEmpty()) {
            configureRootBundle(ctx.project, ctx.config)
        }
    }

    companion object {
        const val BUNDLE_REPO_DIR = "tmp/publication-bundle"
        const val MODULE_BUNDLE_TASK = "createPublicationBundle"
        const val MODULE_BUNDLE_TEMP_CLEAN_TASK = "cleanLocalTempBundleRepository"
        const val MODULE_BUNDLE_TEMP_PUBLISH_TASK = "publishAllPublicationsToLocalTempBundleRepository"
        const val PROJECT_BUNDLE_TASK = "createProjectPublicationBundle"

        fun configureBundle(project: Project, config: ProjectConfigurationExtension) {
            val publication = project.extensions
                .getByType(PublishingExtension::class.java)
                .publications
                .findByName("main") as? MavenPublication
            if (publication == null) {
                PluginLogger.log(LogLevel.WARN, "No 'main' publication found, skipping maven bundle creation")
                return
            }

            // set-up temporary maven local repository
            val tempBundleRepoDir = project.layout.buildDirectory.dir(BUNDLE_REPO_DIR)
            tempBundleRepoDir.get().asFile.mkdirs()
            project.extensions.configure(PublishingExtension::class.java) { publishing ->
                PluginLogger.log(LogLevel.DEBUG, "Registering local bundle repository at: ${tempBundleRepoDir.get().asFile.absolutePath}")
                publishing.repositories.maven { repo ->
                    repo.name = "localTempBundle"
                    repo.url = tempBundleRepoDir.get().asFile.toURI()
                }
            }

            // bundle
            val cleanTask = project.tasks.register(MODULE_BUNDLE_TEMP_CLEAN_TASK, Delete::class.java) {
                it.delete(tempBundleRepoDir)
            }
            val publishTask = project.tasks.named(MODULE_BUNDLE_TEMP_PUBLISH_TASK) {
                it.dependsOn(cleanTask)
            }
            project.tasks.register(MODULE_BUNDLE_TASK, Zip::class.java) { task ->
                task.group = "publishing"
                task.description = "Creates a zip file with all modules in the build/distributions directory"
                task.dependsOn(publishTask)
                task.archiveFileName.set("${project.name}-${project.version}.bundle.zip")
                task.destinationDirectory.set(project.layout.buildDirectory.dir("distributions"))
                task.from(tempBundleRepoDir)
            }
        }

        /**
         * Configures the root project to create a bundle of all subprojects.
         */
        fun configureRootBundle(project: Project, config: ProjectConfigurationExtension) {
            // bundle project
            val rootProject = project.rootProject
            if (rootProject.subprojects.isNotEmpty() && rootProject.tasks.findByName(PROJECT_BUNDLE_TASK) == null) {
                rootProject.tasks.register(PROJECT_BUNDLE_TASK, Zip::class.java) { task ->
                    task.group = "publishing"
                    task.description = "Creates a zip file with all subprojects in the build/distributions directory"
                    task.archiveFileName.set("${rootProject.name}-${rootProject.version}.bundle.zip")
                    task.destinationDirectory.set(rootProject.layout.buildDirectory.dir("distributions"))

                    rootProject.subprojects.forEach { subProject ->
                        val bundleTasks = subProject.tasks.matching { it.name == MODULE_BUNDLE_TEMP_PUBLISH_TASK }
                        if (bundleTasks.isEmpty()) {
                            return@forEach
                        }

                        // For subprojects that have the module bundle task
                        bundleTasks.configureEach { subBundleTask ->
                            task.dependsOn(subBundleTask)
                            val subProjectTempBundleRepoDir = subProject.layout.buildDirectory.dir(BUNDLE_REPO_DIR)
                            task.from(subProjectTempBundleRepoDir)
                        }
                    }
                }
            }
        }
    }
}
