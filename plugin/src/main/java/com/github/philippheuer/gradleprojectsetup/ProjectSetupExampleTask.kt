package com.github.philippheuer.gradleprojectsetup

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.TaskAction

abstract class ProjectSetupExampleTask : DefaultTask() {

    init {
        description = "Just a sample template task"
        group = BasePlugin.BUILD_GROUP
    }

    @TaskAction
    fun sampleAction() {
        logger.lifecycle("sample action was executed!")
    }
}