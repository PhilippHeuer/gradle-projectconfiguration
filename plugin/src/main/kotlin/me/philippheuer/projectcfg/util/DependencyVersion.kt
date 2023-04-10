package me.philippheuer.projectcfg.util

open class DependencyVersion {
    companion object {
        // renovate: datasource=maven depName=org.jetbrains.kotlin:kotlin-stdlib
        @JvmStatic var kotlinVersion: String = "1.8.20"
        // renovate: datasource=maven depName=org.junit.jupiter:junit-jupiter-api
        @JvmStatic var junit5Version: String = "5.9.2"
        // renovate: datasource=maven depName=org.mockito:mockito-core
        @JvmStatic var mockitoVersion: String = "5.2.0"
        // renovate: datasource=maven depName=org.mockito.kotlin:mockito-kotlin
        @JvmStatic var mockitoKotlinVersion: String = "4.1.0"
        // renovate: datasource=maven depName=org.springframework.boot:spring-boot-starter
        @JvmStatic var springBootVersion: String = "3.0.5"
        @JvmStatic var springNativeVersion: String = "0.11.2"
        // renovate: datasource=maven depName=io.quarkus:quarkus-core
        @JvmStatic var quarkusVersion: String = "2.16.6.Final"
        // renovate: datasource=maven depName=io.github.microutils:kotlin-logging
        @JvmStatic var kotlinLoggingVersion: String = "3.0.5"
        // renovate: datasource=maven depName=org.awaitility:awaitility
        @JvmStatic var availabilityVersion: String = "4.2.0"
        // renovate: datasource=maven depName=org.slf4j:slf4j-api
        @JvmStatic var slf4jVersion: String = "2.0.7"
        // renovate: datasource=maven depName=com.lmax:disruptor
        @JvmStatic var disruptorVersion: String = "3.4.4"
        // renovate: datasource=maven depName=io.sentry:sentry
        @JvmStatic var sentryVersion: String = "6.17.0"
        // renovate: datasource=maven depName=org.apache.logging.log4j:log4j
        @JvmStatic var log4j2Version: String = "2.20.0"
        // renovate: datasource=maven depName=org.jboss:jandex
        @JvmStatic var jandexVersion: String = "3.1.0"
        // renovate: datasource=maven depName=io.micrometer:micrometer-bom
        @JvmStatic var micrometerVersion: String = "1.10.6"
    }
}