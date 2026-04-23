package me.philippheuer.projectcfg.util

open class DependencyVersion {
    companion object {
        // renovate: datasource=maven depName=org.jetbrains.kotlin:kotlin-stdlib
        @JvmStatic var kotlinVersion: String = "2.3.21"
        // renovate: datasource=maven depName=org.junit.jupiter:junit-jupiter-api
        @JvmStatic var junitVersion: String = "6.0.3"
        // renovate: datasource=maven depName=io.github.oshai:kotlin-logging
        @JvmStatic var kotlinLoggingVersion: String = "8.0.01"
        // renovate: datasource=maven depName=org.slf4j:slf4j-api
        @JvmStatic var slf4jVersion: String = "2.0.17"
    }
}
