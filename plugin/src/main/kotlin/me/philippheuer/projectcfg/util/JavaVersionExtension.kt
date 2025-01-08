package me.philippheuer.projectcfg.util

import org.gradle.api.JavaVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun JavaVersion.toJVMVersion(): String {
    if (this == JavaVersion.VERSION_1_8) {
        return "1.8"
    }

    return this.toString()
}

fun JavaVersion.toJvmTarget(): JvmTarget {
    return when(this) {
        JavaVersion.VERSION_1_8 -> JvmTarget.JVM_1_8
        JavaVersion.VERSION_1_9 -> JvmTarget.JVM_9
        JavaVersion.VERSION_1_10 -> JvmTarget.JVM_10
        JavaVersion.VERSION_11 -> JvmTarget.JVM_11
        JavaVersion.VERSION_12 -> JvmTarget.JVM_12
        JavaVersion.VERSION_13 -> JvmTarget.JVM_13
        JavaVersion.VERSION_14 -> JvmTarget.JVM_14
        JavaVersion.VERSION_15 -> JvmTarget.JVM_15
        JavaVersion.VERSION_16 -> JvmTarget.JVM_16
        JavaVersion.VERSION_17 -> JvmTarget.JVM_17
        JavaVersion.VERSION_18 -> JvmTarget.JVM_18
        JavaVersion.VERSION_19 -> JvmTarget.JVM_19
        JavaVersion.VERSION_20 -> JvmTarget.JVM_20
        JavaVersion.VERSION_21 -> JvmTarget.JVM_21
        JavaVersion.VERSION_22 -> JvmTarget.JVM_22
        JavaVersion.VERSION_23 -> JvmTarget.JVM_23
        else -> JvmTarget.JVM_23
    }
}

fun JavaVersion.toMajorVersion(): Int {
    return this.majorVersion.toInt()
}

fun JavaVersion.toJavaLanguageVersion(): JavaLanguageVersion {
    return JavaLanguageVersion.of(this.toMajorVersion())
}
