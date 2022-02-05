package me.philippheuer.projectcfg.util

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import java.io.File
import java.nio.file.Path
import java.util.Properties
import kotlin.io.path.writeText

class TaskUtils {
    companion object {
        fun getOutputResourcesFile(project: Project, fileName: String): Path {
            val outputResourcesDir = project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).output.resourcesDir?.toPath()
            return outputResourcesDir!!.resolve(fileName)
        }

        fun writeContentToOutputResources(project: Project, fileName: String, content: String) {
            val outputFile = getOutputResourcesFile(project, fileName)
            outputFile.writeText(content)
        }

        fun processProperties(file: File, defaultProperties: Map<String, String>, overwrite: Boolean) {
            val prop = Properties()
            defaultProperties.forEach { (key, value) -> prop.setProperty(key, value) }
            if (file.isFile && !overwrite) {
                prop.load(file.bufferedReader())
            }
            prop.store(file.bufferedWriter(), null)
        }
    }
}