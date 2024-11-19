package me.philippheuer.projectcfg.util

import org.gradle.api.JavaVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion

fun JavaVersion.toJVMVersion(): String {
    if (this == JavaVersion.VERSION_1_8) {
        return "1.8"
    }

    return this.toString()
}

fun JavaVersion.toMajorVersion(): Int {
    return this.majorVersion.toInt()
}

fun JavaVersion.toJavaLanguageVersion(): JavaLanguageVersion {
    return JavaLanguageVersion.of(this.toMajorVersion())
}
