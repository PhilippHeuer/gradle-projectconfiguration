import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish")
    id("maven-publish")
}

dependencies {
    // plugin
    implementation(gradleApi())

    // kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.21")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.1.21")
    implementation("org.jetbrains.kotlin:kotlin-noarg:2.1.21")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20")

    // jackson
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.0")

    // third party - plugins
    implementation("io.freefair.gradle:lombok-plugin:8.13.1")
    implementation("com.adarshr:gradle-test-logger-plugin:4.0.0")
    implementation("gradle.plugin.com.github.johnrengelman:shadow:8.0.0")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.52.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.8")
    implementation("com.gorylenko.gradle-git-properties:gradle-git-properties:2.5.0")
    implementation("org.cyclonedx:cyclonedx-gradle-plugin:2.3.1")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.13.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.13.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.1.21")
}

configurations {
    implementation.get().exclude(mapOf("group" to "org.jboss.slf4j", "module" to "slf4j-jboss-logmanager"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.run {
    withType<Test> {
        useJUnitPlatform()
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
}

group = "me.philippheuer.projectcfg"

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
