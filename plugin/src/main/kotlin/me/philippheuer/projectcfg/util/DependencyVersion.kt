package me.philippheuer.projectcfg.util

open class DependencyVersion {
    companion object {
        @JvmStatic var kotlinVersion: String = "1.6.10"
        @JvmStatic var junit5Version: String = "5.8.2"
        @JvmStatic var mockitoVersion: String = "4.2.0"
        @JvmStatic var mockitoKotlinVersion: String = "4.0.0"
        @JvmStatic var jacksonVersion: String = "2.13.1"
        @JvmStatic var springBootVersion: String = "2.6.2"
        @JvmStatic var springNativeVersion: String = "0.11.1" // springNativeVersion needs to be compatible with springBootVersion - see https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#_validate_spring_boot_version
        @JvmStatic var quarkusVersion: String = "2.6.1.Final"
        @JvmStatic var kotlinLoggingVersion: String = "2.1.20"
    }
}