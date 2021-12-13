package me.philippheuer.projectcfg.config

import org.gradle.api.provider.Property

interface FrameworkConfig {

    // enables metrics
    val frameworkMetrics: Property<Boolean>

}