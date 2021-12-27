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

    // third party plugins
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.6.1")
    implementation("io.quarkus:gradle-application-plugin:2.5.0.Final")
    implementation("io.freefair.gradle:lombok-plugin:6.3.0")
    implementation("com.adarshr:gradle-test-logger-plugin:3.1.0")
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.1")
    implementation("com.coditory.gradle:manifest-plugin:0.1.14")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.39.0")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
}

configurations {
    implementation.get().exclude(mapOf("group" to "org.jboss.slf4j", "module" to "slf4j-jboss-logmanager"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.run {
    withType<Test> {
        useJUnitPlatform()
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

gradlePlugin {
    plugins {
        create(PluginCoordinates.ID) {
            id = PluginCoordinates.ID
            implementationClass = PluginCoordinates.IMPLEMENTATION_CLASS
            version = PluginCoordinates.VERSION
        }
    }
}

pluginBundle {
    website = PluginBundle.WEBSITE
    vcsUrl = PluginBundle.VCS
    description = PluginBundle.DESCRIPTION
    tags = PluginBundle.TAGS

    plugins {
        getByName(PluginCoordinates.ID) {
            displayName = PluginBundle.DISPLAY_NAME
        }
    }

    mavenCoordinates {
        groupId = PluginCoordinates.GROUP
        artifactId = PluginCoordinates.ID
        version = PluginCoordinates.VERSION
    }
}
