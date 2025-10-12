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
    implementation(project(":projectcfg-gradle-plugin"))

    // spring
    implementation(libs.spring.boot.plugin)
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
        create("springConfigurationPlugin") {
            id = "me.philippheuer.configuration.spring"
            displayName = "Project Configuration Plugin - Spring"
            description = "A Gradle plugin to easily share best-practice project configurations."
            version = project.version
            tags = listOf(
                "setup",
                "buildscript",
                "best-practice"
            )
            implementationClass = "me.philippheuer.projectcfg.spring.SpringProjectConfigurationPlugin"
        }
    }
}
