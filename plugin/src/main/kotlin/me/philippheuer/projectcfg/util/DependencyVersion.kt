package me.philippheuer.projectcfg.util

open class DependencyVersion {
    companion object {
        // renovate: datasource=maven depName=org.jetbrains.kotlin:kotlin-stdlib
        @JvmStatic var kotlinVersion: String = "1.9.22"
        // renovate: datasource=maven depName=org.junit.jupiter:junit-jupiter-api
        @JvmStatic var junit5Version: String = "5.10.1"
        // renovate: datasource=maven depName=io.github.microutils:kotlin-logging
        @JvmStatic var kotlinLoggingVersion: String = "3.0.5"
        // renovate: datasource=maven depName=org.slf4j:slf4j-api
        @JvmStatic var slf4jVersion: String = "2.0.10"
    }
}