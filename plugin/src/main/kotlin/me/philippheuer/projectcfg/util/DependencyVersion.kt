package me.philippheuer.projectcfg.util

open class DependencyVersion {
    companion object {
        @JvmStatic var kotlinVersion: String = "1.6.21" // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib
        @JvmStatic var junit5Version: String = "5.8.2" // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
        @JvmStatic var mockitoVersion: String = "4.4.0" // https://mvnrepository.com/artifact/org.mockito/mockito-core
        @JvmStatic var mockitoKotlinVersion: String = "4.0.0" // https://mvnrepository.com/artifact/org.mockito.kotlin/mockito-kotlin
        @JvmStatic var jacksonVersion: String = "2.13.2" // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
        @JvmStatic var springBootVersion: String = "2.6.6" // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter
        @JvmStatic var springNativeVersion: String = "0.11.2" // https://mvnrepository.com/artifact/org.springframework.experimental/spring-native?repo=springio-libs-release - springNativeVersion needs to be compatible with springBootVersion, see https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#_validate_spring_boot_version
        @JvmStatic var quarkusVersion: String = "2.7.5.Final" // https://mvnrepository.com/artifact/io.quarkus/quarkus-core
        @JvmStatic var kotlinLoggingVersion: String = "2.1.21" // https://mvnrepository.com/artifact/io.github.microutils/kotlin-logging
        @JvmStatic var availabilityVersion: String = "4.2.0" // https://mvnrepository.com/artifact/org.awaitility/awaitility
        @JvmStatic var events4jVersion: String = "0.10.0" // https://mvnrepository.com/artifact/com.github.philippheuer.events4j/events4j-core
        @JvmStatic var slf4jVersion: String = "1.7.36" // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
        @JvmStatic var disruptorVersion: String = "3.4.4" // https://mvnrepository.com/artifact/com.lmax/disruptor
        @JvmStatic var sentryVersion: String = "5.7.0" // https://mvnrepository.com/artifact/io.sentry/sentry
        @JvmStatic var log4j2Version: String = "2.17.2" // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j
        @JvmStatic var jandexVersion: String = "2.4.2.Final" // https://mvnrepository.com/artifact/org.jboss/jandex
        @JvmStatic var micrometerVersion: String = "1.9.0" // https://mvnrepository.com/artifact/io.micrometer/micrometer-bom
    }
}