package me.philippheuer.projectcfg.util

open class DependencyVersion {
    companion object {
        // renovate: datasource=maven depName=org.jetbrains.kotlin:kotlin-stdlib
        @JvmStatic var kotlinVersion: String = "1.7.10"
        // renovate: datasource=maven depName=org.junit.jupiter:junit-jupiter-api
        @JvmStatic var junit5Version: String = "5.8.2"
        // renovate: datasource=maven depName=org.mockito:mockito-core
        @JvmStatic var mockitoVersion: String = "4.6.1"
        // renovate: datasource=maven depName=org.mockito.kotlin:mockito-kotlin
        @JvmStatic var mockitoKotlinVersion: String = "4.0.0"
        // renovate: datasource=maven depName=org.springframework.boot:spring-boot-starter
        @JvmStatic var springBootVersion: String = "2.7.1"
        @JvmStatic var springNativeVersion: String = "0.11.2"
        // renovate: datasource=maven depName=io.quarkus:quarkus-core
        @JvmStatic var quarkusVersion: String = "2.10.2.Final"
        // renovate: datasource=maven depName=io.github.microutils:kotlin-logging
        @JvmStatic var kotlinLoggingVersion: String = "2.1.23"
        // renovate: datasource=maven depName=org.awaitility:awaitility
        @JvmStatic var availabilityVersion: String = "4.2.0"
        // renovate: datasource=maven depName=org.slf4j:slf4j-api
        @JvmStatic var slf4jVersion: String = "1.7.36"
        // renovate: datasource=maven depName=com.lmax:disruptor
        @JvmStatic var disruptorVersion: String = "3.4.4"
        // renovate: datasource=maven depName=io.sentry:sentry
        @JvmStatic var sentryVersion: String = "6.2.0"
        // renovate: datasource=maven depName=org.apache.logging.log4j:log4j
        @JvmStatic var log4j2Version: String = "2.18.0"
        // renovate: datasource=maven depName=org.jboss:jandex
        @JvmStatic var jandexVersion: String = "2.4.3.Final"
        // renovate: datasource=maven depName=io.micrometer:micrometer-bom
        @JvmStatic var micrometerVersion: String = "1.9.2"
    }
}