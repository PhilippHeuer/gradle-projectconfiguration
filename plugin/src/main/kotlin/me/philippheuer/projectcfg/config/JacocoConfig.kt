package me.philippheuer.projectcfg.config

import org.gradle.api.provider.Property

interface JacocoConfig {

    // jacoco version
    val jacocoVersion: Property<String>

}