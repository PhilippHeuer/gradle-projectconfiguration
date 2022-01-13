package me.philippheuer.projectcfg.util

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import java.io.File

class PluginHelper {
    companion object {
        private val isIDEA = System.getProperty("idea.fatal.error.notification") != null
        private val isCI = "true".equals(System.getenv("CI"), true)

        /**
         * managed file
         */
        fun createOrUpdatePropertyFile(
            project: Project,
            defaultPropertyFile: File,
            properties: Map<String, String>,
            managed: Boolean = false,
        ) {
            PluginLogger.log(LogLevel.INFO, "managing config file ${defaultPropertyFile.relativeTo(project.rootDir)}")

            // write config
            val content = StringBuilder()
            if (managed) {
                content.append("# DO NOT EDIT! This file contains defaults and is managed automatically by the project configuration gradle plugin.\n")
            }

            properties.forEach {
                content.append("${it.key}=${it.value}\n")
            }
            defaultPropertyFile.parentFile.mkdirs()
            defaultPropertyFile.writeText(content.toString())
        }

        fun isIDEA(): Boolean {
            return isIDEA
        }

        fun isCI(): Boolean {
            return isCI
        }
    }
}