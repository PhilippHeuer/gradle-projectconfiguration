package com.github.philippheuer.gradleprojectsetup.config

import org.gradle.api.provider.Property

interface ShadowConfig {

    // ShadowFeature: shadow will generate a shadowJar (fatJar)
    val shadow: Property<Boolean>

    // ShadowFeature: prefix to use when generating the shadowJar
    val shadowRelocate: Property<String>

}