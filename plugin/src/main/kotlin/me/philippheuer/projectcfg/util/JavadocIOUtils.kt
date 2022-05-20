package me.philippheuer.projectcfg.util

import org.gradle.api.Project
import java.net.URL

class JavadocIOUtils {

    companion object {

        /**
         * helper method to discover links to javadoc.io hosted javadocs for linking
         *
         * @param project gradle project
         * @param group dependency group
         * @param name dependency name
         * @param version dependency version
         * @return a link to the javadoc root or null if not found
         */
        fun getLinkForDependency(project: Project, group: String?, name: String?, version: String?): String? {
            val link = "https://javadoc.io/doc/${group}/${name}/${version}"
            val depHash = HashUtils.stringToSha256Hash("${group}/${name}/${version}")
            val pastResultFile = project.file("${project.rootDir}/build/javadocio-check/$depHash")

            // check if javadoc exists using file (package-list, element-list)
            var found = false
            if (pastResultFile.exists()) {
                found = pastResultFile.readText() == "true"
            } else {
                listOf("package-list", "element-list").forEach { file ->
                    try {
                        URL("$link/$file").openStream()
                        found = true
                    } catch (ex: Exception) {
                        // ignore
                    }
                }

                pastResultFile.parentFile.mkdirs()
                pastResultFile.writeText(found.toString())
            }

             return if (found) {
                link
            } else {
                null
            }
        }
    }
}
