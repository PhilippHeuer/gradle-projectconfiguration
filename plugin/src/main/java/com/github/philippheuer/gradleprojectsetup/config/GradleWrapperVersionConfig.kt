package com.github.philippheuer.gradleprojectsetup.config

import org.gradle.api.provider.Property

interface GradleWrapperVersionConfig {

    // GradleWrapperVersionPolicy: this can be used to disable the gradle version check
    val gradleVersionCheckBypass: Property<Boolean>

}