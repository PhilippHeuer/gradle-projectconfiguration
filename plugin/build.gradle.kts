import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.plugin.publish)
    id("java-gradle-plugin")
    id("maven-publish")
    id("signing")
}

dependencies {
    // plugin
    implementation(gradleApi())

    // kotlin
    implementation(libs.kotlin.stdlib.jdk8)
    testImplementation(libs.kotlin.test)

    // jackson
    implementation(libs.jackson.dataformat.yaml)

    // test
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.launcher)

    // plugins - kotlin
    implementation(libs.kotlin.plugin)
    implementation(libs.kotlin.allopen.plugin)
    implementation(libs.kotlin.noarg.plugin)
    implementation(libs.dokka.plugin)
    implementation(libs.dokka.javadoc.plugin)

    // plugins - various
    implementation(libs.lombok.plugin)
    implementation(libs.test.logger.plugin)
    implementation(libs.shadow.plugin)
    implementation(libs.detect.plugin)
    implementation(libs.cyclonedx.plugin)

    // misc
    implementation(libs.apache.commons.lang3)
}

configurations {
    implementation.get().exclude(mapOf("group" to "org.jboss.slf4j", "module" to "slf4j-jboss-logmanager"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.run {
    withType<Test> {
        useJUnitPlatform()
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}

// signing
extensions.configure(SigningExtension::class.java) {
    sign(publishing.publications)
}

gradlePlugin {
    website = "https://github.com/PhilippHeuer/gradle-projectconfiguration"
    vcsUrl = "https://github.com/PhilippHeuer/gradle-projectconfiguration.git"

    plugins {
        create("configurationPlugin") {
            id = "me.philippheuer.configuration"
            displayName = "Project Configuration Plugin"
            description = "A Gradle plugin to easily share best-practice project configurations."
            version = project.version
            tags = listOf(
                "setup",
                "buildscript",
                "best-practice"
            )
            implementationClass = "me.philippheuer.projectcfg.ProjectConfigurationPlugin"
        }
    }
}
