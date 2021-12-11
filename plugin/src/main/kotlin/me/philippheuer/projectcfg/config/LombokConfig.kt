package me.philippheuer.projectcfg.config

import org.gradle.api.provider.Property

interface LombokConfig {

    // java version
    val lombokVersion: Property<String>

}