package me.philippheuer.projectcfg.config

import org.gradle.api.provider.Property

interface GradleWrapperVersionConfig {

    // GradleWrapperVersionPolicy: enabled?
    val gradleVersionPolicyEnabled: Property<Boolean>

}