package me.philippheuer.projectcfg.config

import org.gradle.api.provider.Property

interface FrameworkConfig {

    // enables metrics
    val frameworkMetrics: Property<Boolean>

    // enables tracing
    val frameworkTracing: Property<Boolean>

    // enables web api
    val frameworkWebApi: Property<Boolean>

    // enables db (hibernate)
    val frameworkDb: Property<Boolean>

    // enables automatic db migrations
    val frameworkDbMigrate: Property<Boolean>

}