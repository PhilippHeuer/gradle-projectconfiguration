package me.philippheuer.projectcfg.modules.features

import me.philippheuer.projectcfg.ProjectConfigurationExtension
import me.philippheuer.projectcfg.domain.IProjectContext
import me.philippheuer.projectcfg.domain.PluginModule
import me.philippheuer.projectcfg.domain.ProjectType
import me.philippheuer.projectcfg.util.PluginLogger
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
        return ctx.isProjectType(ProjectType.LIBRARY)
    }

    override fun run() {
        configureBundle(ctx.project, ctx.config)
    }

    companion object {
        const val BUNDLE_REPO_DIR = "tmp/publication-bundle"

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
            val cleanTask = project.tasks.register("cleanLocalTempBundleRepository", Delete::class.java) {
                it.delete(tempBundleRepoDir)
            }
            val publishTask = project.tasks.named("publishAllPublicationsToLocalTempBundleRepository") {
                it.dependsOn(cleanTask)
            }
            project.tasks.register("createPublicationBundle", Zip::class.java) { task ->
                task.group = "publishing"
                task.description = "Creates a zip file with all modules in the build/distributions directory"
                task.dependsOn(publishTask)
                task.archiveFileName.set("${project.name}-${publication.version}.bundle.zip")
                task.destinationDirectory.set(project.layout.buildDirectory.dir("distributions"))
                task.from(tempBundleRepoDir)
            }

            // bundle project
            val rootProject = project.rootProject
            if (rootProject.subprojects.isNotEmpty() && rootProject.tasks.findByName("publishProjectBundleToFilesystem") == null) {
                rootProject.tasks.register("createProjectPublicationBundle", Zip::class.java) { task ->
                    task.group = "publishing"
                    task.description = "Creates a zip file with all subprojects in the build/distributions directory"
                    task.dependsOn(publishTask)
                    task.archiveFileName.set("${rootProject.name}-${publication.version}.bundle.zip")
                    task.destinationDirectory.set(rootProject.layout.buildDirectory.dir("distributions"))

                    rootProject.subprojects.forEach { subProject ->
                        // add dependencies
                        subProject.tasks.matching { it.name == "createPublicationBundle" }.configureEach {
                            task.dependsOn(it)
                        }

                        // add repo directories as input
                        val subProjectTempBundleRepoDir = subProject.layout.buildDirectory.dir(BUNDLE_REPO_DIR)
                        if (subProjectTempBundleRepoDir.get().asFile.exists()) {
                            task.from(subProjectTempBundleRepoDir)
                        }
                    }
                }
            }
        }
    }
}
