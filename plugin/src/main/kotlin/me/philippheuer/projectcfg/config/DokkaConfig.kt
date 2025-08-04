package me.philippheuer.projectcfg.config

import org.jetbrains.dokka.gradle.DokkaExtension

interface DokkaConfig {

    // dokka customization
    var dokka: (DokkaExtension) -> Unit

}
